package editor.config

sealed trait ConfigError {
  def errorMessage: String = this match {
    case MissingEnvironmentVariableError(name) => s"Environment variable $name has not been specified"
  }
}
case class MissingEnvironmentVariableError(name: String) extends ConfigError

