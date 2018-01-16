organization in ThisBuild := "com.inventory"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `inventory-management-lagom` = (project in file("."))
  .aggregate(`inventory-management-lagom-api`, `inventory-management-lagom-impl`, `customer-api`, `customer-impl`)

lazy val `inventory-management-lagom-api` = (project in file("inventory-management-lagom-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `inventory-management-lagom-impl` = (project in file("inventory-management-lagom-impl"))
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
  .dependsOn(`inventory-management-lagom-api`)

lazy val `customer-api` = (project in file("customer-api"))
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `customer-impl` = (project in file("customer-impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`customer-api`)
