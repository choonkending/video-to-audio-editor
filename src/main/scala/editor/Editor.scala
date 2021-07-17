package editor

import scala.io.StdIn._
import java.io.File
import cats.implicits._
import cats.effect._

import editor.audio._
import editor.config.{Config, ConverterConfig, PrependerConfig}
import editor.commands._

sealed trait Editor

object Editor:
  def init(config: Config): Editor =
    MainMenu(config)

  def next(editor: Editor): IO[Editor] =
    editor match
      case mainMenu: MainMenu => mainMenu.next
      case matchAndExecuteCommand: MatchAndExecuteCommand => matchAndExecuteCommand.next
      case Done => IO(Done)
end Editor

case class MainMenu(config: Config) extends Editor:
  private def displayMenuOptions(): IO[Unit] = {
    for {
      _ <- IO(println ("\n🙏 🙏 🙏 Welcome to the Audio Visual Team 🙏 🙏 🙏\n") )
      _ <- IO(println ("\nWonderful, we have our required environment variables available 🎉\n") )
      _ <- IO(println("\n Hi there, please select an option from the following:\n"))
      _ <- IO(println("1. Converter Service: convert MP4 to MP3\n"))
      _ <- IO(println("2. Prepender Service: Stitch a selected audio clip add the beginning of an MP3 file\n"))
      _ <- IO(println("Q. Quit application\n"))
      _ <- IO(println("Enter an option 1, 2 or Q and hit the Enter Key 🙏\n"))
    } yield ()
  }

  private def getMenuOptions(): IO[Either[ParsingError, Command]] =
    IO(readLine()).map(CommandParser.parse)

  def next: IO[Editor] = {
    displayMenuOptions()
      .flatMap(_ => getMenuOptions())
      .flatMap {
        case Right(command) => IO(MatchAndExecuteCommand(config, command))
        case Left(parsingError) => IO(
          System.err.println("\n🚨 🚨 Failed to parse command 🚨 🚨 \nLet's try again...\n")
        ).as(MainMenu(config))
      }
  }

end MainMenu

case class MatchAndExecuteCommand(config: Config, command: Command) extends Editor:
  private def filterWithSuffix(suffix: String)(files: List[File]): List[File] =
    files.filter(_.getName.endsWith(suffix))

  private def selectFileFromDirectory(directory: File): IO[File] = {
    val ioFiles = IO(directory.listFiles.toList)
      .map(filterWithSuffix("mp3"))

    val ioFilesWithIndex = ioFiles.map(_.toVector.zipWithIndex)

    val ioFileOptions = ioFilesWithIndex.flatMap(_.traverse(
      (file, index) => IO(println(s"${index} : ${file.getName}"))
    ))

    for {
      files <- ioFiles
      _ <- ioFileOptions
      selectedOption <- IO(readInt())
    } yield files(selectedOption) // try using files.lift(selectedOption)
  }

  private def handleResult(result: Either[Throwable, List[Either[Throwable, LazyList[String]]]]): IO[Editor] = {
    result match {
      case Left(error) =>
        IO(
          System.err.println("🥺 Something went wrong \n Please double check if you have specified the correct directories 🙏")
        )
        .flatMap(_ => IO(error.printStackTrace()))
        .as(Done)
      case Right(_) => IO(println("\nSuccessfully ran your service.\n")).as(Done)
    }
  }

  private def executeCommands(ffmpegCommands: IO[List[FFMPEGCommand]]): IO[Editor] = {
    ffmpegCommands
      .flatMap(_.traverse(FFMPEG.run))
      .attempt
      .flatMap(handleResult)
  }

  private def convertMP4: IO[Editor] = {
    val ConverterConfig(videoDirectory, audioDirectory) = config.converterConfig

    def createFFMPEGCommand(file: File): IO[FFMPEGCommand] = {
      val inputFile = IO(file.getCanonicalPath)
      val outputFile = inputFile.map(
        _.replace(
          videoDirectory.toString(),
          audioDirectory.toString()
        ).replace(".mp4", ".mp3")
      )
      for {
        input <- inputFile
        output <- outputFile
        command = FFMPEGCommand.videoToAudio(input, output)
      } yield command
    }

    val mp4Files = IO(videoDirectory.listFiles.toList)
      .map(filterWithSuffix("mp4"))

    val ffmpegCommands: IO[List[FFMPEGCommand]] = mp4Files
      .flatMap(_.traverse(createFFMPEGCommand))

    executeCommands(ffmpegCommands)
  }

  private def prepend: IO[Editor] = {
    val PrependerConfig(templateDirectory, inputDirectory, outputDirectory) = config.prependerConfig

    def createFFMPEGCommand(file: File): IO[FFMPEGCommand] = {
      for {
        _ <- IO(println(s"Select an audio file to prepend for: ${file.getName}"))
        selectedFile <- selectFileFromDirectory(templateDirectory)
        templateFileName <- IO(selectedFile.getCanonicalPath)
        inputFile <- IO(file.getCanonicalPath)
        outputFile = inputFile.replace(inputDirectory.toString(), outputDirectory.toString())
        command = FFMPEGCommand.prepend(
          templateFileName,
          inputFile,
          outputFile
        )
      } yield command
    }

    val mp3Files = IO(inputDirectory.listFiles.toList)
      .map(filterWithSuffix("mp3"))

    val ffmpegCommands: IO[List[FFMPEGCommand]] = mp3Files
      .flatMap(_.traverse(createFFMPEGCommand))

    executeCommands(ffmpegCommands)
  }

  def next: IO[Editor] =
    command match
      case ConvertMP4ToMP3 => convertMP4
      case PrependMP3WithIntroduction => prepend
      case Quit => IO(Done)

end MatchAndExecuteCommand

object Done extends Editor
