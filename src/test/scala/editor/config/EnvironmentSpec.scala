package editor.config

import org.specs2.mutable.Specification
import scala.collection.immutable

class EnvironmentSpec extends Specification {
  "Environment" >> {
    "required" >> {
      "should error when not found" >> {
        val env = Environment(immutable.Map.empty[String, String])
        env.required("REQUIRED") must beLeft[MissingEnvironmentVariableError]
      }

      "should return value when available" >> {
        val env = Environment(immutable.Map("REQUIRED" -> "value"))
        env.required("REQUIRED") must beRight("value")
      }
    }
  }
}
