package com.devmaid.common.file

import java.text.SimpleDateFormat
import java.util.Date

trait TimePrinter extends FileSynchronizer {

  abstract override def uploadFile(fileToLoad: String, sourceIndex: Int): RemoteResult = {
    val dateFormat = new SimpleDateFormat("HH:mm:ss")
    val response = super.uploadFile(fileToLoad, sourceIndex)
    response.status match {
      case RemoteResult.ERROR => println(dateFormat.format(new Date()) + ": File: " + fileToLoad + " failed result: " + response)
      case RemoteResult.SUCESS => println(dateFormat.format(new Date()) + ": File: " + fileToLoad + " sucess result: " + response)
    }
    response
  }
}
