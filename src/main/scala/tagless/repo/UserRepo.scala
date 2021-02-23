package tagless.repo

import tagless.model.User

trait UserRepo[F[_]]{
  def getUser(id:Int):F[Option[User]]
}


object UserRepo{
  def apply[F[_]](implicit userRepoF:UserRepo[F]): UserRepo[F] = userRepoF
}