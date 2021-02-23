package tagless

import cats.effect.IO
import tagless.model.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Database {

  val db:Map[Int, User] = Map(1 -> User("Fab", "Bob"), 2 -> User("Louis", "Dédé"))

  def readDBIO(id:Int):IO[Option[User]] = IO.pure(db.lift(id))
  def readDBFuture(id:Int):Future[Option[User]] = Future(db.lift(id))


}
