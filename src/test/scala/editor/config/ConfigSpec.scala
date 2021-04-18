package editor.config

import org.specs2.mutable.Specification
import scala.collection.immutable
import java.io.File
import editor.AppError

class ConfigSpec extends Specification {
  "Config" >> {
    "getProductionConfig" >> {
      "should return config when required environment variables are present" >> {
        val env = Environment(
          immutable.Map(
            "VIDEO_DIRECTORY" -> "/path/to/nibbāna",
            "AUDIO_DIRECTORY" -> "/magga",
            "PREPEND_TEMPLATE_FILE" -> "/bsv.mp3",
            "PREPEND_INPUT_DIRECTORY" -> "/prepend_input",
            "PREPEND_OUTPUT_DIRECTORY" -> "/prepend_output"
          )
        )
        Config.getProductionConfig(env) must beRight(
          Config(
            ConverterConfig(new File("/path/to/nibbāna"), new File("/magga")),
            PrependerConfig(new File("/bsv.mp3"), new File("/prepend_input"), new File("/prepend_output"))
           )
        )
      }

      "should return an error when required environment variables are missing" >> {
        val env = Environment(immutable.Map.empty)
        Config.getProductionConfig(env) must beLeft[AppError]
      }
    }
  }
}
