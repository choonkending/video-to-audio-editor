package editor.file

import java.io._
import cats.effect.IO

class FileReader(listFiles: File => IO[Array[File]]) {
  def listFilesInDirectory(file: File, extension: FileExtension): IO[Vector[File]] = {
    listFiles(file)
      .map(
        files =>
          files.filter(_.isFile)
          .filter(file => file.getName().contains(extension.name))
          .toVector
      )
  }
}
