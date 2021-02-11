package editor.audio

import cats.data.NonEmptyVector

case class FFMPEGCommand(input: NonEmptyVector[String], output: String, options: Vector[String])
