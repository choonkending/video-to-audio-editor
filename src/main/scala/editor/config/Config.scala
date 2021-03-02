package editor.config

import java.io.File
import cats.data.Validated.{Valid, Invalid}
import cats.implicits._
import editor._

case class Config(videoDirectory: File, audioDirectory: File)

object Config {
  def getProductionConfig(env: Environment): Either[AppError, Config] = {
    val videoDirectory = env.required("VIDEO_DIRECTORY").map(new File(_)).toValidatedNec
    val audioDirectory = env.required("AUDIO_DIRECTORY").map(new File(_)).toValidatedNec

    val maybeConfig = (videoDirectory, audioDirectory).mapN(Config.apply)

    maybeConfig match
      case Invalid(errors) => Left(ConfigError(errors))
      case Valid(c) => Right(c)
  }
}
