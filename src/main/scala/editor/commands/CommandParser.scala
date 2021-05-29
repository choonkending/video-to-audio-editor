package editor.commands

import cats.implicits._

val commandMappings = Map(
    "1" -> ConvertMP4ToMP3,
    "2" -> PrependMP3WithIntroduction,
    "Q" -> Quit
)

object CommandParser {
    def fromString(input: String): Either[ParsingError, Command] =
        Either.fromOption(
            commandMappings.get(input),
            InvalidCommand
        )
}