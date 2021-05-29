package editor.commands

sealed trait ParsingError
case object InvalidCommand extends ParsingError
