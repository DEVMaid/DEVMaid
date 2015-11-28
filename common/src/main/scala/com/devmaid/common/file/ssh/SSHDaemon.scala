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

package com.devmaid.common.file.ssh
import com.jcabi.ssh.SSH

class SSHDaemon(_hostname: String, _port: Int, _login:String, _keyfile:String, _keycontent:String) {
   val ssh = new SSH(_hostname, _port,_login, _keycontent)
   val hostname = _hostname
   val port = _port
   val login = _login
   val keyfile = _keyfile
   def getSSH() = ssh
}