val scala3Version = "3.0.0-RC3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "video-and-audio-editor",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      ("org.specs2" %% "specs2-core" % "4.11.0" % Test).withDottyCompat(scala3Version),
      "org.typelevel" %% "cats-effect-testing-specs2" % "1.1.0" % Test,
      "org.typelevel" %% "cats-core" % "2.6.0",
      "org.typelevel" %% "cats-effect" % "3.1.0"
    )
  )


