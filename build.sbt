organization in ThisBuild := "com.inventory"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val scalaz = "org.scalaz" %% "scalaz-core" % "7.2.18"

lagomCassandraCleanOnStart in ThisBuild := true
lagomServiceLocatorEnabled in ThisBuild := true

lazy val `inventory-management-lagom` = (project in file("."))
  .aggregate(`customer-api`, `customer-impl`, `product-api`, `product-impl`, `purchase-api`, `purchase-impl`)

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

lazy val `product-api` = (project in file("product-api"))
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `product-impl` = (project in file("product-impl"))
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
  .dependsOn(`product-api`)

lazy val `purchase-api` = (project in file("purchase-api"))
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`product-api`)
  .dependsOn(`customer-api`)

lazy val `purchase-impl` = (project in file("purchase-impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      scalaz
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`purchase-api`)