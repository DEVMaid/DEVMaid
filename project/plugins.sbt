logLevel := Level.Warn
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.1")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0.BETA1")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.0")
addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.7.5")  // for sbt-0.13.x or higher