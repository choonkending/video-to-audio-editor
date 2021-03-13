package editor.audio

import cats.effect.IO
import cats.implicits._

import editor.config.Config

class ConverterService(
  config: Config,
  executeFFMPEGCommand: FFMPEGCommand => IO[Either[Throwable, LazyList[String]]]
) {
  def convert(): IO[List[Either[Throwable, LazyList[String]]]] = {
    IO(config.videoDirectory.listFiles)
      .flatMap {
        files =>
          files
            .toList
            .filter(f => f.getName.endsWith("mp4"))
            .map(f => {
              val input = f.getCanonicalPath
              val output = input.replace(".mp4", ".mp3")
              FFMPEGCommand.videoToAudio(input, output)
            })
              .traverse(executeFFMPEGCommand)
      }

  }
}
