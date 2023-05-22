val Http4sVersion = "0.23.18"

val CirceVersion = "0.14.3"
val CirveFs2Version = "0.14.3"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.11"
val MunitCatsEffectVersion = "1.0.7"
val AttoVersion = "0.9.5"
val PureConfigVersion = "0.17.4"
val ScalaCheckVersion = "1.14.1"
val ScalaTestVersion = "3.2.16"

ThisBuild / organization := "com.elisa"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"

lazy val commonSettings = Seq(
  addCompilerPlugin(
    "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

val serviceDeps =
  Seq(
    "org.http4s" %% "http4s-ember-server" % Http4sVersion,
    "org.http4s" %% "http4s-ember-client" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime,
    "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
    "org.scalactic" %% "scalactic" % ScalaTestVersion,
    "org.scalatest" %% "scalatest" % ScalaTestVersion % "test"
  )

lazy val gen = (project in file("gen")).settings(
  commonSettings,
  libraryDependencies ++= serviceDeps ++ Seq(
    "org.scalacheck" %% "scalacheck" % ScalaCheckVersion
  )
)
lazy val api = (project in file("api")).settings(
  commonSettings,
  libraryDependencies ++= serviceDeps ++ Seq(
    "org.tpolecat" %% "atto-core" % AttoVersion,
    "org.tpolecat" %% "atto-refined" % AttoVersion
  )
)

lazy val root = (project in file("."))
  .aggregate(gen, api)
  .settings(
    publish := false
  )
