package editor.audio

import cats.effect.IO
import cats.implicits._

import editor.config.Config

class PrependerService(
  config: Config,
  executeFFMPEGCommand: FFMPEGCommand => IO[Either[Throwable, LazyList[String]]]
) {
  def prepend(): IO[List[Either[Throwable, LazyList[String]]]] = {
    val prependerConfig = config.prependerConfig
    val template = prependerConfig.templateFile.getCanonicalPath
    IO(prependerConfig.inputDirectory.listFiles)
      .flatMap {
        files =>
          files
            .toList
            .filter(f => f.getName.endsWith("mp3"))
            .map(f => {
              val input = f.getCanonicalPath
              val output = input
                .replace(prependerConfig.inputDirectory.toString(), prependerConfig.outputDirectory.toString())
              FFMPEGCommand.prepend(template, input, output)
            })
            .traverse(executeFFMPEGCommand)
      }
  }
}
