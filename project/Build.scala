import sbt._
import Keys._
import com.typesafe.sbt.SbtStartScript

object BuildSettings {
    import Dependencies._
    import Resolvers._

    val buildOrganization = "net.mccg"
    val buildVersion = "1.0"
    val buildScalaVersion = "2.11.7"

    val globalSettings = Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        scalacOptions += "-deprecation",
        fork in test := true,
        libraryDependencies ++= Seq(),
        resolvers := Seq(akkaRepo, typeSafeRepo, sprayRepo))

    val projectSettings = Defaults.defaultSettings ++ globalSettings
}

object Resolvers {
    val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
    val jbossRepo = "JBoss" at "http://repository.jboss.org/nexus/content/groups/public/"
    val akkaRepo = "Akka" at "http://repo.akka.io/repository/"
    val typeSafeRepo = "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
    val sprayRepo = "spray repo" at "http://repo.spray.io"
}

object Dependencies {
    val akka = "com.typesafe.akka" %% "akka-http-experimental" % "2.0.3"
    val mongo = "org.reactivemongo" %% "reactivemongo" % "0.11.9"
    val amqp = "com.rabbitmq" % "amqp-client" % "2.8.1"
    val caching = "io.spray" %% "spray-caching" % "1.3.3"

}



object MainBuild extends Build {
    import BuildSettings._
    import Dependencies._
    import Resolvers._

    override lazy val settings = super.settings ++ globalSettings

    lazy val root = Project("scala-hands-on",
                            file("."),
                            settings = projectSettings ++
                            Seq(
                                SbtStartScript.stage in Compile := Unit
                            )) aggregate(common, web, workers)

    lazy val web = Project("scala-hands-on-web",
                           file("web"),
                           settings = projectSettings ++
                           SbtStartScript.startScriptForClassesSettings ++
                           Seq(libraryDependencies ++= Seq(akka, amqp))) dependsOn(common % "compile->compile;test->test")

    lazy val workers = Project("scala-hands-on-workers",
                              file("worker"),
                              settings = projectSettings ++
                              SbtStartScript.startScriptForClassesSettings ++
                              Seq(libraryDependencies ++= Seq(amqp))) dependsOn(common % "compile->compile;test->test")

    lazy val common = Project("scala-hands-on-common",
                           file("common"),
                           settings = projectSettings ++
                           Seq(libraryDependencies ++= Seq(akka, mongo, caching)))
}

