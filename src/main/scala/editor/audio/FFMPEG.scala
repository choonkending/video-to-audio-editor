package editor.audio

import cats.effect.IO

object FFMPEG {
  def run(command: FFMPEGCommand): IO[FFMPEGExecutionResult] = {
    IO(println("Some effect happened in the FFMPEG Facade")).map(_ => FFMPEGExecutionSuccess)
  }
}

