package editor

import cats.effect._
import editor.Editor
import editor.config.{Config, Environment}
import editor.audio._
import editor.commands._
import scala.io.StdIn.readLine

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


  private def startEditorApp(config: Config): IO[ExitCode] = {
    for {
      _ <- IO(println("\n Hi there, please select an option from the following:\n"))
      _ <- IO(println("1. Converter Service: convert MP4 to MP3\n"))
      _ <- IO(println("2. Prepender Service: Stitch a selected audio clip add the beginning of an MP3 file\n"))
      _ <- IO(println("Q. Quit application\n"))
      _ <- IO(println("Enter an option 1, 2 or Q and hit the Enter Key 🙏\n"))
      line <- IO(readLine())
      outcome <- CommandParser.parse(line).map(matchService(config)) match
        case Right(ioOutcome) => ioOutcome
        case Left(parsingError) => IO(System.err.println("Failed to parse command")).as(ExitCode.Error)
    } yield outcome
  }

  private def matchService(config: Config)(command: Command): IO[ExitCode] = {
    command match
      case ConvertMP4ToMP3 => runConverterService(config)
      case PrependMP3WithIntroduction => runPrependerService(config)
      case Quit => IO(ExitCode.Success)
  }

  private def runConverterService(config: Config): IO[ExitCode] = {
    val converterService = new ConverterService(config, FFMPEG.run)
    converterService
      .convert()
      .attempt
      .flatMap {
        case Left(error) =>
          IO(
            System.err.println("🥺 Something went wrong \n Please double check if you have created the data/mp4 and data/mp3 directories 🙏")
          )
            .flatMap(
              _ => IO(error.printStackTrace())
            ).as(ExitCode.Error)
        case Right(_) => IO(println("Successfully ran the converter service")).as(ExitCode.Success)
    }
  }

  private def runPrependerService(config: Config): IO[ExitCode] = {
    val prependerService = new PrependerService(config, FFMPEG.run)

    prependerService
      .prepend()
      .attempt
      .flatMap {
        case Left(error) =>
          IO(
            System.err.println("🥺 Something went wrong \n Please double check if you have have " +
              "data/prepend_input, data/prepend_output, data/prepend_templates directories 🙏")
          )
            .flatMap(
              _ => IO(error.printStackTrace())
            ).as(ExitCode.Error)
        case Right(_) => IO(println("Successfully ran the prepender service")).as(ExitCode.Success)
    }
  }

}

