package com.devmaid.config

import scopt.OptionParser

case class ArgumentsConfiguration(configFileName: String, webOnly: String, coreOnly: String)

class ArgumentsParser(args: Array[String]) {

  val parser: OptionParser[ArgumentsConfiguration] = new scopt.OptionParser[ArgumentsConfiguration]("FileUploader") {

    (opt[String]('c', "config") valueName "filename"
      text "Load application configuration from a file with a given name"
      action { (filename, config) => config.copy(configFileName = filename) })
      
    (opt[String]('w', "webonly") valueName "webonly"
      text "Specify if the application should only run the web component only"
      action { (webonly, config) => config.copy(webOnly = webonly) })

    (opt[String]('a', "coreonly") valueName "coreonly"
      text "Specify if the application should only run the core component only"
      action { (coreonly, config) => config.copy(coreOnly = coreonly) })

      
    (help("help") abbr "h"
      text "Display this message")
  }

  private val defaultArgumentsConfiguration = ArgumentsConfiguration("/etc/DEVMaid/config.json", null, null)
  private val argumentsConfiguration = parser.parse(args, defaultArgumentsConfiguration)

  if (!argumentsConfiguration.isDefined) System.exit(0)

  val configFileName = argumentsConfiguration.get.configFileName
  val webOnly = argumentsConfiguration.get.webOnly
  val coreOnly = argumentsConfiguration.get.coreOnly
}
