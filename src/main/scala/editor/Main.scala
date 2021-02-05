package editor

import editor.config.{Config, Environment}

object Main {
  def main(args: Array[String]): Unit = {
    println("\n🙏 🙏 🙏 Welcome to the Audio Visual Team 🙏 🙏 🙏\n")

    val env = Environment(sys.env)

    Config.getProductionConfig(env) match
      case Left(e: Throwable) =>
        System.err.println(s"Failed to start editor: ${e.getMessage}")
        throw e
      case Right(config) =>
        println("\nWonderful, we have our required environment variables available 🎉\n")
  }
}

