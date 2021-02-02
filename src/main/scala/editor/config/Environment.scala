package editor.config

final case class Environment(env: Map[String, String]) {
  def required(key: String): Either[MissingEnvironmentVariableError, String] = {
    env.get(key) match {
      case Some(value) => Right(value)
      case None => Left(MissingEnvironmentVariableError(s"Missing environment variable: $key"))
    }
  }
}
