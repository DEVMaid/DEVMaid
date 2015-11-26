package com.devmaid.common

import java.nio.file.{ Path, Paths, Files, NoSuchFileException }
import java.io._
import scala.collection.JavaConverters._

object Util extends Log {

  def isFile(p: String): Boolean = {
    val f = new File(p)
    f.isFile()
  }

  def isDirectory(p: String): Boolean = {
    val f = new File(p)
    f.isDirectory()
  }

  def isAnyYSubDirectoryOfX(x: String, ys: List[String]): Boolean = {
    for (y <- ys) {
      if (isYSubDirectoryOfX(x, y)) {
        return true
      }
    }
    return false
  }

  def isYSubDirectoryOfX(x: String, y: String): Boolean = {
    def fakePathIfNeeded(s: String): String = {
      val ss = if ((s take 1) == "/") s else "/" + s
      return if ((s takeRight 1) == "/") ss else s + "/"
    }
    //Make both side 'fake' absolutePath for the sake of comparision
    val xx = fakePathIfNeeded(x)
    val yy = fakePathIfNeeded(y)
    return xx.contains(yy)
  }

  def ensureDir(p: String) = {
    val f = new File(p)
    f.mkdirs()
  }

  def getFileExtension(p: String): String = {
    val fn = new File(p).getName
    val i = fn.lastIndexOf(".")
    if (i > -1) {
      fn.substring(i + 1)
    } else {
      fn
    }
  }

  def getFileName(p: String): String = {
    val f = new File(p)
    f.getName
  }

  def getParentPath(p: String): String = {
    val f = new File(p)
    f.getParent()
  }

  def joinPath(p1: String, p2: String): String = {
    val lastC = p1 takeRight 1
    var newP2 = p2
    if (p2.length > 2 && ((p2 take 2) == "./")) {
      newP2 = p2 takeRight p2.length - 2
    }
    if (p1.length() == 0 || lastC == "/") {
      return p1 + newP2
    } else {
      return p1 + "/" + newP2
    }
  }

  def filesToLoad(sourceRoot: String, path: String = "",
    fileTypesToBeWatched: List[String] = List[String](),
    fileTypesToBeIgnored: List[String] = List[String]()): List[String] = {
    val d = joinPath(sourceRoot, path)
    val rootDirectory = findDirectory(d) getOrElse {
      throw new NoSuchFileException(d)
    }
    deepListFiles(rootDirectory, fileTypesToBeWatched, fileTypesToBeIgnored)
      .map(file => file.toPath.toString)
  }

  def translateUserHomeDirIfThereIsOne(d: String): String = {
    if (d.charAt(0) == '~') {
      val homeDir = System.getProperty("user.home");
      val dd = d.replace("~", "")
      val nd = joinPath(homeDir, dd)
      info("nd: " + nd)
      return nd

    } else
      return d
  }

  def removeLocalFile(fp: String): Boolean = {
    val f = new File(fp)
    return f.delete
  }

  def writeContentToLocalFile(fp: String, content: String) = {
    ensureDir(getParentPath(fp))
    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
      try { op(p) } finally { p.close() }
    }
    val data = Array(content)
    printToFile(new File(fp)) { p =>
      data.foreach(p.println)
    }
  }

  def isEmpty(d: String): Boolean = {
    d == null || d == ""
  }

  private def findDirectory(path: String): Option[java.io.File] = {
    val directory = new File(path)
    if (directory.exists) return Some(directory) else return None
  }

  private def deepListFiles(file: java.io.File, fileTypesToBeWatched: List[String] = List[String](),
    fileTypesToBeIgnored: List[String] = List[String]()): List[java.io.File] = {
    val allFiles = file.listFiles
    val alFilteredFiles = allFiles.filter(x => !isAnyYSubDirectoryOfX(x.getAbsolutePath, fileTypesToBeIgnored))
    //debug("In deepListFiles, alFilteredFiles: " + alFilteredFiles)
    return alFilteredFiles.flatMap(file => if (file.isDirectory) file :: deepListFiles(file) else List(file)).toList
  }

}


