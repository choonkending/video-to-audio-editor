package editor.audio

import cats.data.NonEmptyVector

case class FFMPEGCommand(input: NonEmptyVector[String], output: String, options: Vector[String])

object FFMPEGCommand {
  def videoToAudio(inputFileName: String, outputFileName: String): FFMPEGCommand = {
    val input = NonEmptyVector.of("-i", inputFileName)
    val fixedQualityScaleForAudio = "-qscale:a 0"
    val mapAudioToOutput = "-map a"
    val options = Vector(fixedQualityScaleForAudio, mapAudioToOutput)

    FFMPEGCommand(
      input,
      outputFileName,
      options
    )
  }
}
