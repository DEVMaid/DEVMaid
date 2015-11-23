package com.devmaid.web

import org.scalatra._
import scalate.ScalateSupport

class MyScalatraServlet extends DevmaidwebStack {

  get("/") {
    contentType="text/html"
    
    //When using the scalate helper methods, it is not required to having a leading `/`, so `ssp("index")` would work just as well as `ssp("/index")`.
    //ssp("index")
    layoutTemplate("/WEB-INF/templates/views/index.ssp")
  }

}
