package editor.config

import java.io.File
import cats.data.Validated.{Valid, Invalid}
import cats.implicits._
import editor._

case class Config(converterConfig: ConverterConfig, prependerConfig: PrependerConfig)
case class ConverterConfig(videoDirectory: File, audioDirectory: File)
case class PrependerConfig(templateFile: File, inputDirectory: File, outputDirectory: File)

object Config {
  def getProductionConfig(env: Environment): Either[AppError, Config] = {
    val videoDirectory = env.required("VIDEO_DIRECTORY").map(new File(_)).toValidatedNec
    val audioDirectory = env.required("AUDIO_DIRECTORY").map(new File(_)).toValidatedNec

    val prependTemplateFile = env.required("PREPEND_TEMPLATE_FILE").map(new File(_)).toValidatedNec
    val prependInputDirectory = env.required("PREPEND_INPUT_DIRECTORY").map(new File(_)).toValidatedNec
    val prependOutputDirectory = env.required("PREPEND_OUTPUT_DIRECTORY").map(new File(_)).toValidatedNec

    val maybeConverterConfig = (videoDirectory, audioDirectory).mapN(ConverterConfig.apply)
    val maybePrependerConfig = (prependTemplateFile, prependInputDirectory, prependOutputDirectory).mapN(PrependerConfig.apply)

    (maybeConverterConfig, maybePrependerConfig) match
      case (Invalid(errors), Valid(_)) => Left(ConfigError(errors))
      case (Valid(_), Invalid(errors)) => Left(ConfigError(errors))
      case (Invalid(converterErrors), Invalid(prependerErrors)) => Left(ConfigError(converterErrors ++ prependerErrors))
      case (Valid(converterConfig), Valid(prependerConfig)) => Right(Config(converterConfig, prependerConfig))
  }
}
