val scala2Version = "2.13.5"
val scala3Version = "3.0.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "video-and-audio-editor",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      ("org.specs2" %% "specs2-core" % "4.12.0" % Test).cross(CrossVersion.for3Use2_13),
      "org.typelevel" %% "cats-effect-testing-specs2" % "1.1.1" % Test,
      "org.typelevel" %% "cats-core" % "2.6.1",
      "org.typelevel" %% "cats-effect" % "3.1.1"
    ),

    // To cross compile with Scala 3 and Scala 2
    crossScalaVersions := Seq(scala3Version, scala2Version)
  )


