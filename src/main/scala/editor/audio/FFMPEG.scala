package editor.audio

import cats.effect.IO
import scala.sys.process._

object FFMPEG {
  def run(command: FFMPEGCommand): IO[Either[Throwable, LazyList[String]]] = {
    IO(
      Process(FFMPEGCommand.toScalaProcessCommand(command)).lazyLines
    ).attempt
  }
}

