package editor

import scala.io.StdIn._
import java.io.File
import cats.effect._
import cats.implicits._
import editor.audio._
import editor.config.{Config}
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
  private def filterWithSuffix(suffix: String)(files: Array[File]): List[File] =
    files.toList.filter(_.getName.endsWith(suffix))

  private def selectFileFromDirectory(directory: File): IO[File] = {
    val ioFiles = IO(directory.listFiles)
      .map(filterWithSuffix("mp3"))

    val ioFilesWithIndex = ioFiles.map(_.toVector.zipWithIndex)

    val ioFileOptions = ioFilesWithIndex.flatMap(_.traverse(
      (file, index) => IO(println(s"${index} : ${file.getName}"))
    ))

    for {
      files <- ioFiles
      _ <- ioFileOptions
      selectedOption <- IO(readInt())
    } yield files(selectedOption)
  }

  private def convertMP4: IO[Editor] = {
    val converterConfig = config.converterConfig
    IO(converterConfig.videoDirectory.listFiles)
      .map(filterWithSuffix("mp4"))
      .map(_.map(
            file => {
              val input = file.getCanonicalPath
              val output = input
                .replace(converterConfig.videoDirectory.toString(), converterConfig.audioDirectory.toString())
                .replace(".mp4", ".mp3")
              FFMPEGCommand.videoToAudio(input, output)
            })
      )
      .flatMap(_.traverse(FFMPEG.run))
      .attempt
      .flatMap {
        case Left(error) =>
          IO(
            System.err.println("🥺 Something went wrong \n Please double check if you have specified the correct directory 🙏")
          )
          .flatMap(
            _ => IO(error.printStackTrace())
          )
          .as(Done)
        case Right(_) => IO(println("Successfully ran the converter service")).as(Done)
      }
  }

  private def prepend: IO[Editor] = {
    val prependerConfig = config.prependerConfig
    val templateDirectory = prependerConfig.templateDirectory
    IO(prependerConfig.inputDirectory.listFiles)
      .map(filterWithSuffix("mp3"))
      .map(_.map(
        file => {
          selectFileFromDirectory(templateDirectory)
            .flatMap(selectedTemplateFile => {
              val input = file.getCanonicalPath
              val output = input
                .replace(prependerConfig.inputDirectory.toString(), prependerConfig.outputDirectory.toString())
              IO(
                FFMPEGCommand.prepend(
                  templateFileName = selectedTemplateFile.getCanonicalPath,
                  inputFileName = input,
                  outputFileName = output
                )
              )
          })
        })
      )
      .flatMap(_.sequence.flatMap(_.traverse(FFMPEG.run)))
      .attempt
      .flatMap {
        case Left(error) =>
          IO(
            System.err.println("🥺 Something went wrong \n Please double check if you have specified the correct template directory 🙏")
          )
            .flatMap(
              _ => IO(error.printStackTrace())
            )
            .as(Done)
        case Right(_) => IO(println("Successfully ran the prepender service")).as(Done)
      }
  }

  def next: IO[Editor] =
    command match
      case ConvertMP4ToMP3 => convertMP4
      case PrependMP3WithIntroduction => prepend
      case Quit => IO(Done)

end MatchAndExecuteCommand

object Done extends Editor
