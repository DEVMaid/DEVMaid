
assemblyMergeStrategy in assembly := {
    case "META-INF/spring.tooling" => MergeStrategy.last
    case PathList("scala", xs @ _*) => MergeStrategy.last
    case PathList("org", "slf4j", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case "library.properties" => MergeStrategy.last
    case PathList("com", "devmaid", "common", xs @ _*) => MergeStrategy.last
    case "overview.html" => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}

resourceGenerators in Compile <+= (resourceManaged, baseDirectory) map
{ (managedBase, base) =>
  val webappBase = base / "src" / "main" / "webapp"
  for {
    (from, to) <- webappBase ** "*" x rebase(webappBase, managedBase /
"main" / "webapp")
  } yield {
    Sync.copy(from, to)
    to
  }
}