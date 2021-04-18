package editor.audio

import org.specs2.mutable.Specification
import cats.data.NonEmptyVector

class FFMPEGCommandSpec extends Specification {
  "FFMPEGCommand" >> {
    "videoToAudio constructor" >> {
      "should return FFMPEGCommand object" >> {
        val expected = FFMPEGCommand(
            NonEmptyVector.of("-i", "allo.mp4"),
            "allo.mp3",
            List("-qscale:a", "0", "-map", "a")
          )
        FFMPEGCommand.videoToAudio("allo.mp4", "allo.mp3") must beEqualTo(expected)
      }
    }

    "prepend constructor" >> {
      "should return FFMPEG object" >> {
        val expected = FFMPEGCommand(
            NonEmptyVector.of("-i", "intro.mp3", "-i", "allo.mp3"),
            "allo.mp3",
            List("-filter_complex", "concat=n=2:v=0:a=1")
          )
        FFMPEGCommand.prepend("intro.mp3", "allo.mp3", "allo.mp3") must beEqualTo(expected)
      }
    }

    "convertToString" >> {
      "should return a FFMPEGCommand as a string" >> {
        val expected = Seq("ffmpeg", "-i", "allo.mp4", "-qscale:a", "0", "-map", "a", "allo.mp3")

        val command = FFMPEGCommand.videoToAudio("allo.mp4", "allo.mp3")

        FFMPEGCommand.toScalaProcessCommand(command) must beEqualTo(expected)
      }
    }
  }
}
