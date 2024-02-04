package editor

import scala.io.StdIn.*
import java.io.File
import cats.implicits.*
import monix.eval.*
import editor.audio.{FFMPEG, FFMPEGCommand}
import editor.config.{Config, ConverterConfig, PrependerConfig}
import editor.commands.*

sealed trait Editor

object Editor:
  def init(config: Config): Editor =
    MainMenu(config)

  def next(editor: Editor): Task[Editor] =
    editor match
      case mainMenu: MainMenu => mainMenu.next
      case matchAndExecuteCommand: MatchAndExecuteCommand => matchAndExecuteCommand.next
      case Done => Task(Done)
end Editor

case class MainMenu(config: Config) extends Editor:
  private def displayMenuOptions(): Task[Unit] = {
    for {
      _ <- Task(println ("\n🙏 🙏 🙏 Welcome to the Audio Visual Team 🙏 🙏 🙏\n") )
      _ <- Task(println ("\nWonderful, we have our required environment variables available 🎉\n") )
      _ <- Task(println("\n Hi there, please select an option from the following:\n"))
      _ <- Task(println("1. Converter Service: convert MP4 to MP3\n"))
      _ <- Task(println("2. Prepender Service: Stitch a selected audio clip add the beginning of an MP3 file\n"))
      _ <- Task(println("Q. Quit application\n"))
      _ <- Task(println("Enter an option 1, 2 or Q and hit the Enter Key 🙏\n"))
    } yield ()
  }

  private def getMenuOptions(): Task[Either[ParsingError, Command]] =
    Task(readLine()).map(CommandParser.parse)

  def next: Task[Editor] = {
    displayMenuOptions()
      .flatMap(_ => getMenuOptions())
      .flatMap {
        case Right(command) => Task(MatchAndExecuteCommand(config, command))
        case Left(parsingError) => Task(
          System.err.println("\n🚨 🚨 Failed to parse command 🚨 🚨 \nLet's try again...\n")
        ).as(MainMenu(config))
      }
  }

end MainMenu

case class MatchAndExecuteCommand(config: Config, command: Command) extends Editor:
  private def filterWithSuffix(suffix: String)(files: List[File]): List[File] =
    files.filter(_.getName.endsWith(suffix))

  private def selectFileFromDirectory(directory: File): Task[File] = {
    val taskFiles = Task(directory.listFiles.toList)
      .map(filterWithSuffix("mp3"))

    val taskFilesWithIndex = taskFiles.map(_.toVector.zipWithIndex)

    val taskFileOptions = taskFilesWithIndex.flatMap(l => Task.traverse(l)(
      (file, index) => Task(println(s"${index} : ${file.getName}"))
    ))

    for {
      files <- taskFiles
      _ <- taskFileOptions
      selectedOption <- Task(readInt())
    } yield files(selectedOption) // try using files.lift(selectedOption)
  }

  private def handleResult(result: Either[Throwable, List[Either[Throwable, String]]]): Task[Editor] = {
    result match {
      case Left(error) =>
        Task(
          System.err.println("🥺 Something went wrong \n Please double check if you have specified the correct directories 🙏")
        )
        .flatMap(_ => Task(error.printStackTrace()))
        .as(Done)
      case Right(_) => Task(println("\nSuccessfully ran your service.\n")).as(MainMenu(config))
    }
  }

  private def executeCommands(ffmpegCommands: Task[List[FFMPEGCommand]]): Task[Editor] = {
    ffmpegCommands
      .flatMap(l => Task.traverse(l)(FFMPEG.run))
      .attempt
      .flatMap(handleResult)
  }

  private def convertMP4: Task[Editor] = {
    val ConverterConfig(videoDirectory, audioDirectory) = config.converterConfig

    def createFFMPEGCommand(file: File): Task[FFMPEGCommand] = {
      val inputFile = Task(file.getCanonicalPath)
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

    val mp4Files = Task(videoDirectory.listFiles.toList)
      .map(filterWithSuffix("mp4"))

    val ffmpegCommands: Task[List[FFMPEGCommand]] = mp4Files
      .flatMap(l => Task.traverse(l)(createFFMPEGCommand))

    executeCommands(ffmpegCommands)
  }

  private def prepend: Task[Editor] = {
    val PrependerConfig(templateDirectory, inputDirectory, outputDirectory) = config.prependerConfig

    def createFFMPEGCommand(file: File): Task[FFMPEGCommand] = {
      for {
        _ <- Task(println(s"Select an audio file to prepend for: ${file.getName}"))
        selectedFile <- selectFileFromDirectory(templateDirectory)
        templateFileName <- Task(selectedFile.getCanonicalPath)
        inputFile <- Task(file.getCanonicalPath)
        outputFile = inputFile.replace(inputDirectory.toString(), outputDirectory.toString())
        command = FFMPEGCommand.prepend(
          templateFileName,
          inputFile,
          outputFile
        )
      } yield command
    }

    val mp3Files = Task(inputDirectory.listFiles.toList)
      .map(filterWithSuffix("mp3"))

    val ffmpegCommands: Task[List[FFMPEGCommand]] = mp3Files
      .flatMap(l => Task.traverse(l)(createFFMPEGCommand))

    executeCommands(ffmpegCommands)
  }

  def next: Task[Editor] =
    command match
      case ConvertMP4ToMP3 => convertMP4
      case PrependMP3WithIntroduction => prepend
      case Quit => Task(Done)

end MatchAndExecuteCommand

object Done extends Editor
