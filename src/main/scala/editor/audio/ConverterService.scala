package editor.audio

import cats.effect.IO
import cats.implicits._

import editor.config.Config

class ConverterService(
  config: Config,
  executeFFMPEGCommand: FFMPEGCommand => IO[Either[Throwable, LazyList[String]]]
) {
  def convert(): IO[List[Either[Throwable, LazyList[String]]]] = {
    val converterConfig = config.converterConfig
    IO(converterConfig.videoDirectory.listFiles)
      .flatMap {
        files =>
          files
            .toList
            .filter(f => f.getName.endsWith("mp4"))
            .map(f => {
              val input = f.getCanonicalPath
              val output = input
                .replace(converterConfig.videoDirectory.toString(), converterConfig.audioDirectory.toString())
                .replace(".mp4", ".mp3")
              FFMPEGCommand.videoToAudio(input, output)
            })
            .traverse(executeFFMPEGCommand)
      }
  }
}
