package org.lightweightest

import com.sun.net.httpserver.Headers
import groovy.json.JsonSlurper

class LwtRequest {
  private URI uri
  private byte[] content
  private Headers headers
  def params = [:]

  public LwtRequest(URI uri, byte[] content, Headers headers) {
    this.headers = headers
    this.content = content
    this.uri = uri
    parseQuery()
  }

  private parseQuery() {
    if (uri.query) {
      params = uri.query.split('&').inject([:]) {map, kv->
        def (key, value) = kv.split('=').toList()
        map[key] = value != null ? URLDecoder.decode(value) : null
        map
      }
    }
  }

  String getText() {
    new String(content, "UTF-8")
  }

  def getXml() {
    new XmlSlurper().parseText(getText())
  }

  def getJson() {
    new JsonSlurper().parseText(getText())
  }

}
