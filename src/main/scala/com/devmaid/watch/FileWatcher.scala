/*
Copyright (c) 2015 Ken Wu
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#
# -----------------------------------------------------------------------------
#
# Author: Ken Wu
# Date: 2015
* 
*/

package com.devmaid.watch

import java.nio.file.StandardWatchEventKinds._
import java.nio.file._

import com.devmaid.HandlerFactory
import com.devmaid.common.Log
import com.devmaid.common.Util
import com.devmaid.common.file.{ FileSynchronizer, RemoteResult }
import com.devmaid.common.config.Configuration
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._

case class NonExistSourceRootForTheGivenPath(smth: String) extends Exception(smth)

case class PathEvent(key: WatchKey, event: WatchEvent[_]) extends Log {
  def path = {
    val relativePath = event.context().asInstanceOf[Path]
    key.watchable().asInstanceOf[Path].resolve(relativePath)
  }

  def kind = event.kind
}

class FileWatcher(val fileUploader: FileSynchronizer, val sourceRoots: List[String], val refreshIntervalsInSeconds: Int,
    val fileTypesToBeWatchedRaw: List[String], val fileTypesToBeIgnored: List[String]) extends Log {

  val fileTypesToBeWatched = fileTypesToBeWatchedRaw.map(x => x.replaceAll("\\*.", ""))
  val watchedFiles = collection.mutable.Map[String, () => RemoteResult]()
  val handlerFactory = new HandlerFactory(fileUploader)
  val watcher: WatchService = FileSystems.getDefault.newWatchService()
  val refreshIntervalsInMillSeconds: Int = refreshIntervalsInSeconds * 1000

  def isToBeWatched(file: Path): Boolean = {
    if (Util.isAnyYSubDirectoryOfX(file.toFile.getCanonicalPath, fileTypesToBeIgnored)) {
      //This folder is under being filtered (ignored)
      return false
    }
    if (file.toFile.isDirectory) {
      debug("this file: " + file + " (directory? - " + file.toFile.isDirectory + ") is to be watched!!!")
      return true
    } else {
      //Not a directory
      if (watchedFiles.exists(_._1 == file.toString)) {
        return true
      } else {
        val ext = Util.getFileExtension(file.toString)
        //debug("this file: " + file + " has extension: " + ext)
        return fileTypesToBeWatched.contains(ext) == true
      }
    }
  }

  /*
   * sourceIndex refers to the i-th position of the element in the sourcesRoots
   */
  def addToWatched(path: Path, sourceIndex: Int, remove: Boolean = false): Unit = {
    val relativePath = Paths.get(sourceRoots(sourceIndex)).relativize(path).toString
    if (isToBeWatched(path)) {
      if (path.toFile.isDirectory) {
        path.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE)
        val handler = {
          if (remove)
            handlerFactory.getHandlerForDir(relativePath, handlerFactory.UploadOperation.remove, sourceIndex)
          else
            handlerFactory.getHandlerForDir(relativePath, handlerFactory.UploadOperation.upload, sourceIndex)
        }
        info("In addToWatched for directory, handler: " + handler + " on path: " + path + ", remove: " + remove)
        watchedFiles += (path.toString -> handler)
      } else {
        val handler = {
          if (remove)
            handlerFactory.getHandlerForFile(relativePath, handlerFactory.UploadOperation.remove, sourceIndex)
          else
            handlerFactory.getHandlerForFile(relativePath, handlerFactory.UploadOperation.upload, sourceIndex)
        }
        info("In addToWatched for file, handler: " + handler + " on path: " + path + ", remove: " + remove)
        info("Start the running - fileTypesToBeWatched: " + fileTypesToBeWatched + ", watchedFiles: " + watchedFiles)
        watchedFiles += (path.toString -> handler)
      }
    }
  }

  def run() = {
    try {
      info("Start the running - fileTypesToBeWatched: " + fileTypesToBeWatched + ", watchedFiles: " + watchedFiles)
      startWatching()
    } catch {
      case cwse: java.nio.file.ClosedWatchServiceException => {
        info("Watch Serverice is closed!. Exiting")
      }
    }
  }

  private def startWatching() = {
    while (true) {
      //info("In startWatching - watchedFiles: " + watchedFiles)
      val key = watcher.poll(refreshIntervalsInSeconds, TimeUnit.SECONDS);
      //info("keyyy: " + key)
      if (key != null) {
        key.pollEvents()
          .map(PathEvent(key, _))
          .foreach {
            event =>
              val sourceIndex = Configuration.searchForSourceIndex(sourceRoots, event.path.toString)
              info("event.path: " + event.path + ", found sourceIndex: " + sourceIndex + ", event.kind: " + event.kind);
              if (sourceIndex == -1) {
                //This should never happen...
                throw new NonExistSourceRootForTheGivenPath("This event path: " + event.path + " does not belong to any of these sourceRoots: " + sourceRoots)
              }
              event.kind match {
                case ENTRY_DELETE =>
                  addToWatched(event.path, sourceIndex, true)
                  notifyHandlerAssignedTo(event.path)
                case ENTRY_MODIFY =>
                  notifyHandlerAssignedTo(event.path)
                case ENTRY_CREATE =>
                  if (isToBeWatched(event.path)) {
                    addToWatched(event.path, sourceIndex)
                    notifyHandlerAssignedTo(event.path)
                  }
                case x =>
                  println(s"Unexpected event $x")
              }
          }
        key.reset()
      }
    }
  }

  /*
  More info: http://stackoverflow.com/questions/16777869/java-7-watchservice-ignoring-multiple-occurrences-of-the-same-event
   */
  //private def goSleepToAvoidEventDuplication() = Thread.sleep(100)

  def notifyHandlerAssignedTo(path: Path) = {
    info("In notifyHandlerAssignedTo - path: " + path)
    watchedFiles.get(path.toString) foreach {
      handler => handler()
    }
  }

  def close() = {
    watcher.close()
  }

}
