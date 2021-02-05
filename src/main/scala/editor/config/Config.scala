package editor.config

import cats.data.Validated.{Valid, Invalid}
import cats.implicits._

case class Config(videoDirectory: String, audioDirectory: String)

object Config {
  def getProductionConfig(env: Environment): Either[Throwable, Config] = {
    val videoDirectory = env.required("VIDEO_DIRECTORY").toValidatedNec
    val audioDirectory = env.required("AUDIO_DIRECTORY").toValidatedNec

    val maybeConfig = (videoDirectory, audioDirectory).mapN(Config.apply)

    maybeConfig match
      case Invalid(errors) =>
        val errorMessage = errors.toList.map(_.message).mkString("\nConfiguration Errors:\n", "\n", "\n")
        Left(new IllegalStateException(errorMessage))
      case Valid(c) => Right(c)
  }
}
