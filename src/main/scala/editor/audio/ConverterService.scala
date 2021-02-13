package editor.audio

import java.io.File
import cats.effect.IO
import cats.data.NonEmptyVector

import editor.config.Config

class ConverterService(
  config: Config,
  executeFFMPEGCommand: FFMPEGCommand => IO[FFMPEGExecutionResult]
) {
  def convert(): IO[FFMPEGExecutionResult] = {
    val command = FFMPEGCommand(NonEmptyVector.of("-i input"), "output", Vector())
    executeFFMPEGCommand(command)
  }
}