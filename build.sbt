lazy val catsEffectVersion = "1.0.0-RC3"

lazy val circeVersion = "0.10.0-M2"

lazy val http4sVersion = "0.19.0-M1"

lazy val logbackVersion = "1.2.3"

lazy val slf4jVersion = "1.7.25"

lazy val commonSettings = Seq(
  githubProject := "cedi-dtrace",
  contributors ++= Seq(
    Contributor("sbuzzard", "Steve Buzzard"),
    Contributor("mpilquist", "Michael Pilquist")
  ),
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "org.scalacheck" %% "scalacheck" % "1.13.5" % "test"
  ),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
)

lazy val root = project.in(file(".")).aggregate(core, logging, logstash, xb3, money, http4s).settings(commonSettings).settings(noPublish)

lazy val core = project.in(file("core")).enablePlugins(SbtOsgi).
  settings(commonSettings).
  settings(
    name := "dtrace-core",
    libraryDependencies += "org.typelevel" %% "cats-effect-laws" % catsEffectVersion % "test",
    buildOsgiBundle("com.ccadllc.cedi.dtrace")
  )

lazy val logging = project.in(file("logging")).enablePlugins(SbtOsgi).
  settings(commonSettings).
  settings(
    name := "dtrace-logging",
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "ch.qos.logback" % "logback-core" % logbackVersion % "test",
      "ch.qos.logback" % "logback-classic" % logbackVersion % "test",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.1" % "optional"
    ),
    buildOsgiBundle("com.ccadllc.cedi.dtrace.logging")
  ).dependsOn(core % "compile->compile;test->test")

lazy val logstash = project.in(file("logstash")).enablePlugins(SbtOsgi).
  settings(commonSettings).
  settings(
    name := "dtrace-logstash",
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "net.logstash.logback" % "logstash-logback-encoder" % "5.1",
      "ch.qos.logback" % "logback-core" % logbackVersion % "test",
      "ch.qos.logback" % "logback-classic" % logbackVersion % "test"
    ),
    buildOsgiBundle("com.ccadllc.cedi.dtrace.logstash")
  ).dependsOn(core % "compile->compile;test->test")

lazy val xb3 = project.in(file("xb3")).enablePlugins(SbtOsgi).
  settings(commonSettings).
  settings(
    name := "dtrace-xb3",
    libraryDependencies += "org.scodec" %% "scodec-bits" % "1.1.5",
    buildOsgiBundle("com.ccadllc.cedi.dtrace.interop.xb3")
  ).dependsOn(core % "compile->compile;test->test")

lazy val money = project.in(file("money")).enablePlugins(SbtOsgi).
  settings(commonSettings).
  settings(
    name := "dtrace-money",
    buildOsgiBundle("com.ccadllc.cedi.dtrace.interop.money")
  ).dependsOn(core % "compile->compile;test->test")

lazy val http4s = project.in(file("http4s")).enablePlugins(SbtOsgi).
  settings(commonSettings).
  settings(
    name := "dtrace-http4s",
    parallelExecution in Test := false,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion % "test"
    ),
    buildOsgiBundle("com.ccadllc.cedi.dtrace.interop.http4s")
  ).dependsOn(core % "compile->compile;test->test", money % "compile->test", xb3 % "compile->test")

lazy val readme = project.in(file("readme")).settings(commonSettings).settings(noPublish).enablePlugins(TutPlugin).settings(
  tutTargetDirectory := baseDirectory.value / ".."
).dependsOn(core, logging)
