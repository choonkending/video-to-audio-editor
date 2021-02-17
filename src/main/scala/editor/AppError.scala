package editor

import cats.data.NonEmptyChain
import editor.config.MissingEnvironmentVariableError
import cats.implicits._

sealed trait AppError {
  def errorMessage: String = this match
    case ConfigError(errors) => errors
      .toList
      .map(_.errorMessage)
      .mkString("\nConfiguration Errors:\n", "\n", "\n")
}

final case class ConfigError(errors: NonEmptyChain[MissingEnvironmentVariableError]) extends AppError
