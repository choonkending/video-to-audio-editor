package editor.commands

sealed trait Command

object Quit extends Command
object ConvertMP4ToMP3 extends Command
object PrependMP3WithIntroduction extends Command
