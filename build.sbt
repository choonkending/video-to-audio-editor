name := "video-and-audio-editor"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "4.10.0" % Test
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Ywarn-unused:imports",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-language:higherKinds")

mainClass in Compile := Some("editor.Main")
