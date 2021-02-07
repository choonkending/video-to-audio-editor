package editor.audio

import cats.effect.IO

case class FFMPEGArgument(arg: String)

trait FFMPEGFacade {
  def run(): IO[FFMPEGExecutionResult]
}

class FFMPEG(args: Vector[FFMPEGArgument]) extends FFMPEGFacade {
  def run(): IO[FFMPEGExecutionResult] = {
    IO(println("Some effect happened in the FFMPEG Facade")).map(_ => FFMPEGExecutionSuccess)
  }
}

