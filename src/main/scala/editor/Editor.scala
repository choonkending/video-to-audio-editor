package editor

import scala.io.StdIn.readLine

import cats.effect._
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
  def next: IO[Editor] = IO(Done)
end MatchAndExecuteCommand

object Done extends Editor
