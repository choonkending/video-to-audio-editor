package editor.audio

import cats.effect.IO
import scala.sys.process._

object FFMPEG {
  def run(command: FFMPEGCommand): IO[FFMPEGExecutionResult] = {
    IO(
      Process(FFMPEGCommand.toScalaProcessCommand(command)).lazyLines.map(l => println(l))
    ).map(
      _ => FFMPEGExecutionSuccess
    )
  }
}

