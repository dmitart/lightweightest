package org.lightweightest

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class LightweightestSpec extends Specification {
  @Shared
  def server

  def "running server"() {
    when:
    server = Lightweightest.start(port:9999) {
      get("/test") {
        "qwerty"
      }
    }
    then:
    "http://localhost:9999/test".toURL().text == "qwerty"
  }

  def "updating configuration"() {
    when:
    server.get("/test") {
      "asdfgh"
    }

    then:
    "http://localhost:9999/test".toURL().text == "asdfgh"

    cleanup:
    server.stop()
  }

  def "auto shutdown"() {
    when:
    Lightweightest.start(port:9999, stopAfter:1) {
      get("/test") {
        "qwerty"
      }
    }
    "http://localhost:9999/test".toURL().text
    "http://localhost:9999/test".toURL().text

    then:
    thrown(ConnectException)
  }
}
