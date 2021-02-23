package tagless.repo

import cats.effect.IO
import tagless.Database
import tagless.model.User

import scala.concurrent.Future



object repoInterpreter {

  implicit object userRepoIO extends UserRepo[IO]{
    override def getUser(id: Int): IO[Option[User]] = Database.readDBIO(id)
  }
  implicit object userRepoFuture extends UserRepo[Future]{
    override def getUser(id: Int): Future[Option[User]] = Database.readDBFuture(id)
  }
}
