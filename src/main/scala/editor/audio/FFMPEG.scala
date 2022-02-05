package editor.audio

import monix.eval.Task
import scala.sys.process._

object FFMPEG {
  def run(command: FFMPEGCommand): Task[Either[Throwable, LazyList[String]]] = {
    Task(
      Process(FFMPEGCommand.toScalaProcessCommand(command)).lazyLines
    ).attempt
  }
}

