name := "video-and-audio-editor"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.2"
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Ywarn-unused:imports",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-language:higherKinds")

mainClass in Compile := Some("example.Main")
