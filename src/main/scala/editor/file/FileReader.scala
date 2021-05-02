package editor.file

import java.io._
import cats.effect.IO

class FileReader(listFiles: () => IO[Array[File]]) {
  def listFilesInDirectory(extension: FileExtension): IO[List[File]] = {
    listFiles()
      .map(
        files =>
          files
          .toList
          .filter(_.isFile)
          .filter(file => file.getName().contains(extension.name))
      )
  }
}
