package editor.commands

import org.specs2.mutable.Specification

class CommandParserSpec extends Specification {
    "CommandParser" >> {
        "fromString" >> {
            "should return ConvertMP4ToMP3 command when input is 1" >> {
                CommandParser.fromString("1") must beRight(ConvertMP4ToMP3)
            }
        }
    }
}