package editor.file

import java.io.File
import cats.effect.IO
import org.specs2.mutable.Specification
import cats.effect.testing.specs2.CatsEffect

class FileReaderSpec extends Specification with CatsEffect {
  "FileReader" >> {
    "listFilesInDirectory" >> {
      "should return list of files with an extension" in {
        val tempFile = File.createTempFile("allo", ".mp4")
        val anotherTempFile = File.createTempFile("allo", ".mp3")
        val fileReader = new FileReader(() => IO(Array(tempFile, anotherTempFile)))
        val files = fileReader.listFilesInDirectory(FileExtension.MP4)
        files.flatMap {
            result => IO(result mustEqual List(tempFile))
        }
      }
    }
  }
}
