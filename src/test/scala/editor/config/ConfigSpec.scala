package editor.config

import org.specs2.mutable.Specification
import scala.collection.immutable

class ConfigSpec extends Specification {
  "Config" >> {
    "getProductionConfig" >> {
      "should return config when required environment variables are present" >> {
        val env = Environment(
          immutable.Map("VIDEO_DIRECTORY" -> "/path/to/nibbāna", "AUDIO_DIRECTORY" -> "/magga")
        )
        Config.getProductionConfig(env) must beRight(Config("/path/to/nibbāna", "/magga"))
      }

      "should return an error when required environment variables are missing" >> {
        val env = Environment(immutable.Map.empty)
        Config.getProductionConfig(env) must beLeft[Throwable]
      }
    }
  }
}
