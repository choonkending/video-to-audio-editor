val scala3Version = "3.0.0-M3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "video-and-audio-editor",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      ("org.specs2" %% "specs2-core" % "4.10.0" % Test).withDottyCompat(scala3Version),
      "org.typelevel" %% "cats-core" % "2.3.1",
      "org.typelevel" %% "cats-effect" % "2.3.1"
    ),
  )


