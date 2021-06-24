package editor

import cats.effect._
import editor.Editor
import editor.config.{Config, Environment}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val env = Environment(sys.env)
    val productionConfig = Config.getProductionConfig(env)

    productionConfig match
      case Left(e) =>
        IO(System.err.println(s"Failed to start editor: ${e.errorMessage}")).as(ExitCode.Error)
      case Right(config) =>
        val start = Editor.init(config)
        executeEditor(Editor.next(start))
          .as(ExitCode.Success)
  }

  def executeEditor(ioEditor: IO[Editor]): IO[Editor] =
    ioEditor.flatMap {
      editor =>
        editor match
          case Done => IO(Done)
          case other => executeEditor(Editor.next(other))
    }

}

