resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"
resolvers += Classpaths.sbtPluginReleases
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.11")
addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.99.0")
