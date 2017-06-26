resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"
resolvers += Classpaths.sbtPluginReleases
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.5.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
