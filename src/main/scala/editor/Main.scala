package editor

import cats.effect._
import editor.config.{Config, Environment}
import editor.audio._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    println("\n🙏 🙏 🙏 Welcome to the Audio Visual Team 🙏 🙏 🙏\n")

    val env = Environment(sys.env)

    Config.getProductionConfig(env) match
      case Left(e) =>
        IO(System.err.println(s"Failed to start editor: ${e.errorMessage}")).as(ExitCode.Error)
      case Right(config) =>
        println("\nWonderful, we have our required environment variables available 🎉\n")
        val converterService = new ConverterService(config, FFMPEG.run)
        converterService.convert().flatMap {
          case FFMPEGExecutionSuccess => IO(println("Successfully ran the converter service")).as(ExitCode.Success)
          case FFMPEGExecutionFailure => IO(println("Failed to run the converter service")).as(ExitCode.Error)
        }
  }
}

