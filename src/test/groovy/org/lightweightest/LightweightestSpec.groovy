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

  def "checking request"() {
    when:
    def server = Lightweightest.start(port:9999, stopAfter:1) {
      post("/test") {
        "qwerty"
      }
    }
    def conn = "http://localhost:9999/test".toURL().openConnection()
    conn.setDoOutput(true)
    conn.getOutputStream() << "test".bytes

    then:
    conn.getContent().text == 'qwerty'
    server.requests.size() == 1
    server.requests[0].text == 'test'
  }

  def "checking request as xml"() {
    when:
    def server = Lightweightest.start(port:9999, stopAfter:1) {
      post("/test") {
        "qwerty"
      }
    }
    def conn = "http://localhost:9999/test".toURL().openConnection()
    conn.setDoOutput(true)
    conn.getOutputStream() << "<root><elem>test</elem></root>".bytes

    then:
    conn.getContent().text == 'qwerty'
    server.requests.size() == 1
    server.requests[0].xml.elem[0].text() == 'test'
  }

  def "checking request as json"() {
    when:
    def server = Lightweightest.start(port:9999, stopAfter:1) {
      post("/test") {
        "qwerty"
      }
    }
    def conn = "http://localhost:9999/test".toURL().openConnection()
    conn.setDoOutput(true)
    conn.getOutputStream() << '{"root":{"elem":"test"}}'.bytes

    then:
    conn.getContent().text == 'qwerty'
    server.requests.size() == 1
    server.requests[0].json.root.elem == 'test'
  }

  def "checking request params"() {
    when:
    def server = Lightweightest.start(port:9999, stopAfter:1) {
      get("/test") { request ->
        "qwerty ${request.params.id}"
      }
    }

    then:
    "http://localhost:9999/test?id=1&value=aa".toURL().text == "qwerty 1"
    server.requests[0].params.id == '1'
    server.requests[0].params.value == 'aa'
  }

  def "check exception"() {
    when:
    Lightweightest.start(port:9999, stopAfter:1) {
      get("/test") { request ->
        def object = null
        "qwerty ${object.id}"
      }
    }
    "http://localhost:9999/test".toURL().text

    then:
    thrown(IOException)
  }
}
