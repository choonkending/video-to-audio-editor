package editor

import cats.effect.ExitCode
import monix.execution.Scheduler.Implicits.global
import monix.execution.CancelableFuture
import monix.eval.{Task, TaskApp}
import editor.Editor
import editor.config.{Config, Environment}

object Main extends TaskApp {
  def run(args: List[String]): Task[ExitCode] = {
    val env = Environment(sys.env)
    val productionConfig = Config.getProductionConfig(env)

    productionConfig match
      case Left(e) =>
        Task(System.err.println(s"Failed to start editor: ${e.errorMessage}")).as(ExitCode.Error)
      case Right(config) =>
        val start = Editor.init(config)
        executeEditor(Editor.next(start))
          .as(ExitCode.Success)
  }

  def executeEditor(taskEditor: Task[Editor]): Task[Editor] =
    taskEditor.flatMap {
      editor =>
        editor match
          case Done => Task(Done)
          case other => executeEditor(Editor.next(other))
    }

}

