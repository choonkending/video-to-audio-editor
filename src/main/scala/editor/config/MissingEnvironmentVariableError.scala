package editor.config

final case class MissingEnvironmentVariableError(name: String) {
  def errorMessage: String = s"Environment variable $name has not been specified"
}

