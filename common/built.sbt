name := "DevMaid-common"
version := "1.2"
enablePlugins(GitVersioning)
git.useGitDescribe := true

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

scalaVersion := "2.11.6"

resolvers += Resolver.sonatypeRepo("public")
resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.1"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.12" % "provided"
libraryDependencies += "com.decodified" %% "scala-ssh" % "0.7.0"
libraryDependencies += "com.jcraft" % "jzlib" % "1.1.3"
libraryDependencies += "com.jcabi" % "jcabi-ssh" % "1.5"


packAutoSettings