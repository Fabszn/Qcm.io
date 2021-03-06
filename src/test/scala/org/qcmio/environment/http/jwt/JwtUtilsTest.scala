package org.qcmio.environment.http.jwt

import org.qcmio.environment.config.config.JwtConf
import org.qcmio.model.User
import org.scalatest.wordspec.AnyWordSpec

class JwtUtilsTest extends AnyWordSpec {

  "Token should be valid" in {
    val jwtConf = JwtConf("1232344", "")
    val token = JwtUtils.buildToken(User.Email( "fab@fab.com"), jwtConf)
    assert(JwtUtils.isValidToken(token, jwtConf))
  }
}
