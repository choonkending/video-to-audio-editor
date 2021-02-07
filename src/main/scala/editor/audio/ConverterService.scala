package editor.audio

import java.io.File
import cats.effect.IO

import editor.config.Config

class ConverterService(config: Config, ffmpeg: FFMPEGFacade) {
  def convert(): IO[FFMPEGExecutionResult] = {
    ffmpeg.run()
  }
}
