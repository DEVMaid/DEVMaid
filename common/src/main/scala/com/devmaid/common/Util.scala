package com.devmaid.common

import java.nio.file.{ Path, Paths, Files, NoSuchFileException }
import java.io._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

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

  /*
   * This method ensures the PARENT of the path exists regardless the path is file or directory
   */
  def ensureParentDir(p: String) = {
    val f = new File(p)
    if (!f.exists) {
      //If not exists
      ensureDir(f.getParent)
    }
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
      if (p1.length() > 0 && (newP2 take 1) == "/") {
        return p1 + (newP2 takeRight newP2.length - 1)
      } else {
        return p1 + newP2
      }
    } else {
      if ((newP2 take 1) == "/") {
        return p1 + newP2
      } else {
        return p1 + "/" + newP2
      }
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
    d == null || d == "" || d.length() == 0
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

  /*
   * o can be output like: 
   * 
   *  -rw-r--r--   1 kenwu  staff   229B Dec  7 16:05 README.md
			drwxr-xr-x  15 kenwu  staff   510B Dec  7 18:10 src/
			drwxr-xr-x   5 kenwu  staff   170B Dec  7 16:42 ../
			drwxr-xr-x  23 kenwu  staff   782B Dec  7 20:30 ./
			
			When onlyFilesDirs is set to true, only README.md and src/ are returned (i.e. ../ and ./ are ignored)
   */
  def parseLsLrAOutput(o: String, onlyFilesDirs: Boolean): Option[List[(Boolean, String)]] = {
    if (Util.isEmpty(o)) {
      return None
    } else {
      val lines = o.split("\n")
      val results = new ListBuffer[(Boolean, String)]()
      for (line <- lines) {
        val responseAry = line.split("\\s+")
        if (responseAry.length > 2) {
          val isDirectory = (responseAry(0)(0) == 'd')
          var fileName = responseAry(8)
          if(!onlyFilesDirs || (!fileName.equals("./") && !fileName.equals("../"))) {
            if (fileName.charAt(fileName.length() - 1) == '*') {
              //Remove the * character at the end of the path.
              // For example, the path might look like /home/ubuntu/workspace/gis-tools-for-hadoop.wiki/TutorialImages/Image2F2H.png*
              fileName = fileName.substring(0, fileName.length() - 1);
            }
            results += ((isDirectory, fileName))
          } 
        }
      }
      return Some(results.toList)
    }
  }

}


