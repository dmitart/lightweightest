This is very thin and lightweight HTTP server in Groovy for integration and functional tests.
It is based on [JDK HttpServer](http://docs.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html), with little Groovy DSL flavouring.

To start server on port 9999 that returns "qwerty" on GET requests to "/test", just run

    Lightweightest.start(port:9999) {
      get("/test") {
        "qwerty"
      }
    }

This is it.

For testing, you usually know exact execution scenario, so, for example, to serve just one request and destroy server automatically, run

    Lightweightest.start(port:9999, stopAfter:1) {
      get("/test") {
        "qwerty"
      }
    }

To stop server explicitly, run

    def server = Lightweightest.start(port:9999) {
      get("/test") {
        "qwerty"
      }
    }
    server.stop()

It is also possible to update handlers dynamically, like

    def server = Lightweightest.start(port:9999) {
      get("/test") {
        "qwerty"
      }
    }
    ...
    server.get("/test") {
      "asdfg"
    }
    server.stop()

Complete example with of running server from random Groovy script:

    @GrabResolver(name="lightweightest", m2Compatible='true', root='https://raw.github.com/dmitart/lightweightest/master/repository')
    @Grab("org.lightweightest:lightweightest:0.1" )
    import org.lightweightest.Lightweightest

    Lightweightest.start(port:9999, stopAfter:1) {
      get("/card/list") {
        "qwerty"
      }
    }
    println "http://localhost:9999/card/list".toURL().text
