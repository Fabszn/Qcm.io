package org.qcmio.environment.http.jwt

import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.model.Candidat
import org.scalatest.wordspec.AnyWordSpec

class JwtUtilsTest extends AnyWordSpec {

  "Token should be valid" in {
    val jwtConf = JwtConf("1232344", "")
    val token = JwtUtils.buildToken(Candidat.Email( "fab@fab.com"), jwtConf)
    println(token)
    assert(JwtUtils.isValidToken(token, jwtConf))
  }
}
