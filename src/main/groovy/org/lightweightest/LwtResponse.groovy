package org.lightweightest

import com.sun.net.httpserver.Headers

class LwtResponse {
  Headers headers
  int status

  public LwtResponse(int status, Headers headers) {
    this.headers = headers
    this.status = status
  }

}
