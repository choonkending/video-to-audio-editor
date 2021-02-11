package editor.audio

import cats.effect.IO
import cats.data.NonEmptyVector

case class FFMPEGCommand(input: NonEmptyVector[String], output: String, options: Vector[String])

object FFMPEG {
  def run(command: FFMPEGCommand): IO[FFMPEGExecutionResult] = {
    IO(println("Some effect happened in the FFMPEG Facade")).map(_ => FFMPEGExecutionSuccess)
  }
}

