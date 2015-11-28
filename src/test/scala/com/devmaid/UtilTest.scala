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

import org.scalatest.Tag
import java.nio.file.NoSuchFileException
import org.scalatest.{ BeforeAndAfter, Matchers, FunSpec }
import com.devmaid.common.Util
import com.devmaid.common.Log
import com.devmaid.TestUtil._

class UtilTest extends FunSpec with BaseSpec with Matchers with BeforeAndAfter with Log {

  val TEST_RESOURCES_BASE = List[String]("/tmp/devmaid/utilTest")
  val EMPTY_DIRECTORY_PATH = "emptyDirectory"

  it("created on empty directory", Tag("create_empty_directory_Test")) {

    info("SIZE: " + TEST_RESOURCES_BASE.size)
    reset(TEST_RESOURCES_BASE, EMPTY_DIRECTORY_PATH, true)

    val r = Util.filesToLoad(TEST_RESOURCES_BASE(0), EMPTY_DIRECTORY_PATH)
    assert(r == Nil)

    reset(TEST_RESOURCES_BASE, EMPTY_DIRECTORY_PATH, false)
  }

  it("return a deep list of files and folders") {

    val FILE1 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/testa.txt")
    val FILE2 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/b/testb.txt")
    val FILE3 = Util.joinPath(TEST_RESOURCES_BASE(0), "a/c/testc.txt")

    reset(TEST_RESOURCES_BASE, "", true)
    println(Util.filesToLoad(TEST_RESOURCES_BASE(0)))
    Util.filesToLoad(TEST_RESOURCES_BASE(0)) should not contain allOf(FILE1, FILE2, FILE3)

    writeToFile(FILE1, "Content-" + FILE1)
    writeToFile(FILE2, "Content-" + FILE2)
    writeToFile(FILE3, "Content-" + FILE3)

    Util.filesToLoad(TEST_RESOURCES_BASE(0)) should contain allOf (FILE1, FILE2, FILE3)
    reset(TEST_RESOURCES_BASE, "", false)
  }

  it("translateUserHomeDirIfThereIsOne", Tag("translateUserHomeDirIfThereIsOne_Test")) {
    val p1 = "~/hello"
    assert(Util.translateUserHomeDirIfThereIsOne(p1).indexOf("~") == -1)
  }

  it("test the FilesToLoad function specifically the fileTypesToBeWatched and fileTypesToBeIgnored paremeters", Tag("testFilesToLoad_full_test_1")) {
    val FILE1 = Util.joinPath(TEST_RESOURCES_BASE(0), "target/testa.txt")
    val FILE2 = Util.joinPath(TEST_RESOURCES_BASE(0), "lib/target/testb.txt")
    val FILE3 = Util.joinPath(TEST_RESOURCES_BASE(0), "testc.txt")
    val FILE4 = Util.joinPath(TEST_RESOURCES_BASE(0), "lib/target_1/testd.txt")

    val fileTypesToBeWatched = List("")
    val fileTypesToBeIgnored = List("target/", ".git/")

    reset(TEST_RESOURCES_BASE, "", true)

    writeToFile(FILE1, "Content-" + FILE1)
    writeToFile(FILE2, "Content-" + FILE2)
    writeToFile(FILE3, "Content-" + FILE3)
    writeToFile(FILE4, "Content-" + FILE4)

    Util.filesToLoad(TEST_RESOURCES_BASE(0), "", fileTypesToBeWatched, fileTypesToBeIgnored) should not contain allOf(FILE1, FILE2)
    reset(TEST_RESOURCES_BASE, "", false)

  }

  it("test the joinPath function", Tag("joinPath")) {
    assert(Util.joinPath("/hello/", "/dew")=="/hello/dew")
    assert(Util.joinPath("hello", "/dew/")=="hello/dew/")
    assert(Util.joinPath("hello", "dew/")=="hello/dew/")
    assert(Util.joinPath("hello", "dew")=="hello/dew")
    assert(Util.joinPath("hello/", "dew")=="hello/dew")
    assert(Util.joinPath("/hello/", "/dew/")=="/hello/dew/")
  }

}
