package editor

import cats.effect._
import editor.config.{Config, Environment}
import editor.audio._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val env = Environment(sys.env)
    val productionConfig = Config.getProductionConfig(env)

    productionConfig match
      case Left(e) =>
        IO(System.err.println(s"Failed to start editor: ${e.errorMessage}")).as(ExitCode.Error)
      case Right(config) =>
        for {
          _ <- IO(println("\n🙏 🙏 🙏 Welcome to the Audio Visual Team 🙏 🙏 🙏\n"))
          _ <- IO(println("\nWonderful, we have our required environment variables available 🎉\n"))
          exitCode <- runPrependerService(config)
        } yield exitCode
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

