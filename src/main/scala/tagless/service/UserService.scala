package tagless.service


import cats.Monad
import tagless.repo.UserRepo


object UserService {

 import cats.implicits._

  def getUserName[F[_] : UserRepo : Monad](id: Long): F[String] = {
    for {
      name <- UserRepo[F].getUser(id)
    } yield name.fold("No Name")(u => u.name)

  }
}
