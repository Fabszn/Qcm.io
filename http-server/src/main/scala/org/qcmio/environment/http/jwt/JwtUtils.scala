package org.qcmio.environment.http.jwt

import org.qcmio.model.Candidat
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import io.circe.syntax._
import java.time.Instant

object JwtUtils {

  def buildToken(email:Candidat.Email):String = {
    val claim = JwtClaim(
      content = ("email" -> email.asJson).asJson.spaces4,
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond)
      , issuedAt = Some(Instant.now.getEpochSecond)
    )
    // claim: JwtClaim = JwtClaim({}, None, None, None, Some(1791123256), None, Some(1633338496), None)
    val key = "secretKey"
    // key: String = "secretKey"
    val algo = JwtAlgorithm.HS256
    // algo: JwtAlgorithm.HS256.type = HS256

    JwtCirce.encode(claim, key, algo)
  }

}
