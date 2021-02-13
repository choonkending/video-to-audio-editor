package editor.file

import org.specs2.mutable.Specification

class FileExtensionSpec extends Specification {
  "FileExtension" >> {
    "MP4" >> {
      "should return correct extension" >> {
        FileExtension.MP4.name must beEqualTo(".mp4")
      }
    }

    "MP3" >> {
      "should return correct extension" >> {
        FileExtension.MP3.name must beEqualTo(".mp3")
      }
    }
  }
}
