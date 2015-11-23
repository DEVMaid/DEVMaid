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

package com.devmaid

import com.devmaid.web.JettyLauncher
import com.devmaid.common.Util
import com.devmaid.common.config.Configuration
import com.devmaid.config.ArgumentsParser

object Main {

  var configFileName = "";

  def main(args: Array[String]) {

    val arguments = new ArgumentsParser(args)
    configFileName= arguments.configFileName

    if (!Util.isEmpty(arguments.webOnly) && arguments.webOnly.toLowerCase == "true") {
      web(args)
    } else if (!Util.isEmpty(arguments.coreOnly) && arguments.coreOnly.toLowerCase == "true") {
      core()
    } else {
      web(args)
      core()
    }

  }

  private def web(args: Array[String]) = {
    val thread = new Thread {
      override def run {
        JettyLauncher.main(args)
      }
    }
    thread.start
  }

  private def core() = {
    new Application(Configuration.load(configFileName)(0)).run()
  }

}
