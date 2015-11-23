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

package com.devmaid.common.config

import com.devmaid.common.Log
import org.scalatest.{ FunSpec, Matchers }
import org.scalatest.Tag

class ConfigurationTest extends FunSpec with Matchers with Log {

  it("test constructing a list of Configuration from a complex configuration 1", Tag("ConvertFromDefaultTestJsonFile")) {

    var configurations = Configuration.load("./src/test/resources/test-config-localhost-complex.json")

    info("converted Configurations: " + configurations)

    assert(configurations.size == 2)
  }

  it("This should throw an exception because the config has non-equal size of the source and dest folders specified", Tag("ConvertFromTestJsonFile_invalidFormat")) {

    intercept[InvalidConfigurationFormatException] {
      var configurations = Configuration.load("./src/test/resources/test-config-localhost-complex_invalidformat.json")
    }
  }

  it("This should throw an exception because some of the source folder(s) are overlapping with others (i.e. they are from the same path)", Tag("ConvertFromTestJsonFile_invalidFormat_duplicateSourceFolder")) {

    intercept[InvalidConfigurationFormatException] {
      var configurations = Configuration.load("./src/test/resources/test-config-localhost-complex_invalidformat-2.json")
    }
  }

}
