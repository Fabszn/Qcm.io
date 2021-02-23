
import cats.effect.IO
import cats.implicits._
import tagless.service.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

object MainApp extends App {

  import tagless.repo.repoInterpreter._

  val d: Future[String] = UserService.getUserName[Future](1.toLong)
  d.onComplete(f => f match{
    case Success(value) => println(value)
    case _ => println("error")
  })




}


