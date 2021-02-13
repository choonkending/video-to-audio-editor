package editor.file

sealed trait FileExtension

object FileExtension {
  case object MP4 extends FileExtension
  case object MP3 extends FileExtension

  implicit class FileExtensionOps(fileExtension: FileExtension) {
    def name: String = fileExtension match {
      case MP4 => ".mp4"
      case MP3 => ".mp3"
    }
  }
}
