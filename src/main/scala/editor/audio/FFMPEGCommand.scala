package editor.audio

import cats.data.NonEmptyVector
import cats.implicits._

case class FFMPEGCommand(input: NonEmptyVector[String], output: String, options: List[String])

object FFMPEGCommand {
  def videoToAudio(inputFileName: String, outputFileName: String): FFMPEGCommand = {
    val input = NonEmptyVector.of("-i", inputFileName)
    val fixedQualityScaleForAudio = ("-qscale:a", "0")
    val mapAudioToOutput = ("-map", "a")
    val options = fixedQualityScaleForAudio.toList ++ mapAudioToOutput.toList

    FFMPEGCommand(
      input,
      outputFileName,
      options
    )
  }

  def prepend(templateFileName: String, inputFileName: String, outputFileName: String): FFMPEGCommand = {
    val input = NonEmptyVector.of("-i", templateFileName, "-i", inputFileName)
    val concatenateFileOptions = List("-filter_complex", "concat=n=2:v=0:a=1")

    FFMPEGCommand(
      input,
      outputFileName,
      concatenateFileOptions
    )
  }

  def toScalaProcessCommand(command: FFMPEGCommand): Seq[String] = {
    Seq("ffmpeg") ++ command.input.toVector ++ command.options ++ Seq(command.output)
  }
}
