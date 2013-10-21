package org.lightweightest

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import java.util.concurrent.CountDownLatch

class Lightweightest {
  HttpServer server
  CountDownLatch latch = null
  def methods = ['GET':[:], 'POST':[:]]
  def requests = []

  public Lightweightest() {
  }

  void init(def params) {
    if (params.stopAfter) {
      latch = new CountDownLatch(params.stopAfter)
    }
    InetSocketAddress addr = new InetSocketAddress(params.port)
    server = HttpServer.create(addr, 0)
    server.createContext("/", new HttpHandler() {
      @Override
      void handle(HttpExchange exchange) throws Exception {
        def func = methods[exchange.requestMethod][exchange.requestURI.path.toString()]
        def request = new LwtRequest(exchange.requestURI, exchange.requestBody.bytes, exchange.requestHeaders)
        requests << request
        def headers = exchange.getResponseHeaders()
        headers.set("Content-Type", "text/plain")
        try {
          def result = func(request)
          exchange.sendResponseHeaders(200, 0)
          exchange.responseBody << result
        } catch (e) {
          e.printStackTrace()
          StringWriter writer = new StringWriter()
          exchange.sendResponseHeaders(500, 0)
          e.printStackTrace(new PrintWriter(writer))
          exchange.responseBody << writer.toString()
        }
        exchange.responseBody.close()
        if (latch) {
          latch.countDown()
          if (latch.count < 1) {
            Lightweightest.this.stop()
          }
        }
      }
    })
  }

  public void get(String str, Closure closure) {
    methods['GET'][str] = closure
  }

  public void post(String str, Closure closure) {
    methods['POST'][str] = closure
  }

  public void start() {
    server.start()
  }

  public void stop() {
    server.stop(0)
  }

  public static Lightweightest start(Map params, Closure config) {
    Lightweightest server = new Lightweightest()
    if (config) {
      config.setDelegate(server)
      config()
    }
    server.init(params)
    server.start()
    return server
  }

}