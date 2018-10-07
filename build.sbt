organization in ThisBuild := "com.sterowney"
version in ThisBuild := "1.0.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `pet` = (project in file("."))
  .aggregate(`pet-api`, `pet-impl`)

lazy val `pet-api` = (project in file("pet-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `pet-impl` = (project in file("pet-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`pet-api`)

// lagomCassandraCleanOnStart in ThisBuild := true
// lagomCassandraEnabled in ThisBuild := false
// lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")
