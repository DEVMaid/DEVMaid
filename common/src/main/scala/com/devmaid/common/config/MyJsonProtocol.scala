package com.devmaid.common.config

import spray.json.DefaultJsonProtocol

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val connectionFormat3 = jsonFormat3(Connection)
  implicit val configurationFormat = jsonFormat6(Configuration.apply)
  implicit val rawConfigurationFormat = jsonFormat6(RawConfiguration.apply)
}
