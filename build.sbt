name := "DEVMaid"
version := "1.2"
enablePlugins(GitVersioning)
git.useGitDescribe := true

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

scalaVersion := "2.11.6"

resolvers += Resolver.sonatypeRepo("public")
resolvers += "spray repo" at "http://repo.spray.io"


libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0"
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.3.0-SNAP2" % "test"


packAutoSettings

assemblyMergeStrategy in assembly := {
    case PathList("scala", xs @ _*) => MergeStrategy.first
    case PathList("org", "slf4j", xs @ _*) => MergeStrategy.last
    case PathList("org", "eclipse", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("org", "joda", xs @ _*) => MergeStrategy.last
    case PathList("org", "scalatra", xs @ _*) => MergeStrategy.first
    case PathList("rl", xs @ _*) => MergeStrategy.first
    case PathList("com", "devmaid", "web", xs @ _*) => MergeStrategy.last
    case PathList("grizzled", "slf4j", xs @ _*) => MergeStrategy.last
    case "about.html" => MergeStrategy.rename
    case "META-INF/mimetypes.default" => MergeStrategy.last
    case "plugin.properties" => MergeStrategy.last
    case "log4j.properties" => MergeStrategy.last
    case "library.properties" => MergeStrategy.last
    case "META-INF/maven/org.slf4j/slf4j-api/pom.properties" => MergeStrategy.last
    case "META-INF/maven/org.slf4j/slf4j-api/pom.xml" => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
