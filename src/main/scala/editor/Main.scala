package editor

import editor.config.{Config, Environment}
import editor.audio._

object Main {
  def main(args: Array[String]): Unit = {
    println("\n🙏 🙏 🙏 Welcome to the Audio Visual Team 🙏 🙏 🙏\n")

    val env = Environment(sys.env)

    Config.getProductionConfig(env) match
      case Left(e) =>
        System.err.println(s"Failed to start editor: ${e.errorMessage}")
      case Right(config) =>
        println("\nWonderful, we have our required environment variables available 🎉\n")
        val converterService = new ConverterService(config, FFMPEG.run)
        converterService.convert().unsafeRunSync() match {
          case FFMPEGExecutionSuccess => println("Successfully ran the converter service")
          case FFMPEGExecutionFailure => println("Failed to run the converter service")
        }
  }
}

