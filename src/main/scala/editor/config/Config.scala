package editor.config

import java.io.File
import cats.data.Validated.{Valid, Invalid}
import cats.implicits._

case class Config(videoDirectory: File, audioDirectory: File)

object Config {
  def getProductionConfig(env: Environment): Either[Throwable, Config] = {
    val videoDirectory = env.required("VIDEO_DIRECTORY").toValidatedNec
    val audioDirectory = env.required("AUDIO_DIRECTORY").toValidatedNec

    val maybeConfig = (videoDirectory, audioDirectory).mapN(
      (v, a) => Config(new File(v), new File(a))
    )

    maybeConfig match
      case Invalid(errors) =>
        val errorMessage = errors.toList.map(_.errorMessage).mkString("\nConfiguration Errors:\n", "\n", "\n")
        Left(new IllegalStateException(errorMessage))
      case Valid(c) => Right(c)
  }
}
