package editor.audio

import monix.eval.Task
import scala.sys.process._
import scala.util.Try

object FFMPEG {
  def run(command: FFMPEGCommand): Task[Either[Throwable, String]] = {
    Task(
      Try(
        Process(FFMPEGCommand.toScalaProcessCommand(command)).!!
      ).toEither
    )
  }
}

