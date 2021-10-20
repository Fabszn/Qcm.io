package org.qcmio.environment.http.jwt

import io.circe.syntax._
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.model.Candidat
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim, JwtOptions}
import io.circe._,  jawn.{parse => jawnParse}

import java.time.Instant
import javax.crypto.SecretKey
import scala.util.{Failure, Success}

object JwtUtils {

  def buildToken(email:Candidat.Email, conf:JwtConf):String = {

    val claim = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond)
      , issuedAt = Some(Instant.now.getEpochSecond)
    ) + ("email", email.value)
    val key = conf.secretKey
    val algo = JwtAlgorithm.HS256

    JwtCirce.encode(claim, key, algo)
  }

  def isValidToken(token:String, jwtConf:JwtConf):Boolean =
    JwtCirce.decodeJson(token, jwtConf.secretKey, Seq(JwtAlgorithm.HS256)) match {
      case Failure(exception) => exception.printStackTrace();false
      case Success(_) => true
    }



}
