val scala2Version = "2.13.6"
val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "video-and-audio-editor",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      ("org.specs2" %% "specs2-core" % "4.12.0" % Test).cross(CrossVersion.for3Use2_13),
      "io.monix" %% "monix" % "3.4.0"
    ),

    // To cross compile with Scala 3 and Scala 2
    crossScalaVersions := Seq(scala3Version, scala2Version)
  )

enablePlugins(JavaAppPackaging)
