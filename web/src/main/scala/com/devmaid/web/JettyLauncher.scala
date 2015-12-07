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

package com.devmaid.web // remember this package in the sbt project definition

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ DefaultServlet, ServletContextHandler }
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import spray.json.JsonParser

import scopt.OptionParser
import spray.json.DefaultJsonProtocol

import java.io.File

import com.devmaid.web.ssh.SshClient
import com.devmaid.web.util.Log
import com.devmaid.web.util.UtilWeb
import com.devmaid.web.remotefs.RemoteFile
import com.devmaid.web.service.FileRepository
import com.devmaid.common.config.Connection
import com.devmaid.common.config.Configuration
//import com.devmaid.common.config.MyJsonProtocol
import scala.collection.JavaConverters._

case class ArgumentsConfiguration(configFileName: String)

object JettyLauncher extends Log { // this is my entry object as specified in sbt project definition
  
  def instance = this
  
  var connections = List[Connection]()
  var sourceRoots = List("")
  var allFilesUnderSourceRoots = List(Array(new File("")))
  var destinationRoots = List("")
  var allFilesUnderDestinationRoots = List(Array(new RemoteFile("")))
  var configFiles = None : Option[List[Configuration]]
  
  def sourceRootsAsJava = sourceRoots.asJava
  def destinationRootsAsJava = destinationRoots.asJava
  def connectionsAsJava = connections.asJava
  def getConfig (configPos: Int) = configFiles.get(configPos)
  
  //def main(args: Array[String]) {
  def run() = {
    val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("/etc/DEVMaid/webapp")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)

    server.start
    server.join
  }

  //private def loadConfigFile(configFileName: String): String = {
  //  scala.io.Source.fromFile(configFileName).getLines().mkString
  //}
  
  
  def main(args: Array[String]) {

    /* Here the logic to parse the configuration file and load it in */
    val parser: OptionParser[ArgumentsConfiguration] = new scopt.OptionParser[ArgumentsConfiguration]("FileUploader") {
      (opt[String]('c', "config") valueName "filename"
        text "Load application configuration from a file with a given name"
        action { (filename, config) => config.copy(configFileName = filename) })

      (help("help") abbr "h"
        text "Display this message")

    }
    val defaultArgumentsConfiguration = ArgumentsConfiguration("/etc/DEVMaid/config.json")
    val argumentsConfiguration = parser.parse(args, defaultArgumentsConfiguration)
    val configFileName = argumentsConfiguration.get.configFileName
    info("configFileName: " + configFileName)

    configFiles = Some(Configuration.load(configFileName))
    
    SshClient.setConfigFiles(configFiles)
    
    connections = (for(i <- 0 to configFiles.get.size-1) yield configFiles.get(i).connection).toList 
    
    sourceRoots = configFiles.get(0).sourceRoots
    allFilesUnderSourceRoots = (for(i <- 0 to sourceRoots.size-1) yield FileRepository.getInstance().getLocalFile(0, sourceRoots(i)).listFiles()).toList;
    
    destinationRoots = configFiles.get(0).destinationRoots
    allFilesUnderDestinationRoots = (for(i <- 0 to destinationRoots.size-1) yield FileRepository.getInstance().getRemoteFile(0, destinationRoots(i)).listFiles()).toList;
    
    info("connections...:" + connections)
    info("sourceRoots...: " + sourceRoots)
    info("allFilesUnderSourceRoots...: " + UtilWeb.printListOfList(allFilesUnderSourceRoots))
    info("destinationRoots...: " + destinationRoots)
    info("allFilesUnderDestinationRoots...: " + UtilWeb.printListOfList(allFilesUnderDestinationRoots))
    
    run()
  }
}
