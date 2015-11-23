import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object DevmaidwebBuild extends Build {
  val Organization = "com.devmaid"
  val Name = "DevMaidWeb"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.6"
  val ScalatraVersion = "2.4.0.RC3"

  lazy val project = Project (
    "devmaidweb",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        /*Here are for the EFinder dependencies */
        "commons-fileupload" % "commons-fileupload" % "1.3.1",
        "commons-io" % "commons-io" % "2.4",
        "org.apache.commons" % "commons-lang3" % "3.4",
        "org.json" % "json" % "20090211",
        "commons-codec" % "commons-codec" % "1.10",
         "log4j" % "log4j" % "1.2.17",
         "javax.mail" % "javax.mail-api" % "1.5.4",
          "com.mortennobel" % "java-image-scaling" % "0.8.6",
          "javax.servlet" % "javax.servlet-api" % "3.1.0",
          "org.springframework" % "spring-webmvc" % "3.2.3.RELEASE",
          
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.10.v20150310" % "container;compile",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "com.github.scopt" %% "scopt" % "3.3.0",
        "io.spray" %% "spray-json" % "1.3.1"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  )
}
