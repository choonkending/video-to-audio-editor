package editor.commands

import org.specs2.mutable.Specification

class CommandParserSpec extends Specification {
    "CommandParser" >> {
        "parse" >> {
            "should return ConvertMP4ToMP3 command when input is 1" >> {
                CommandParser.parse("1") must beRight(ConvertMP4ToMP3)
            }

            "should return ParsingError if an invalid input is given" >> {
                CommandParser.parse("preu") must beLeft[ParsingError]
            }
        }
    }
}