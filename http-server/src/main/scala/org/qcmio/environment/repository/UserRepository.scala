package org.qcmio.environment.repository

import org.qcmio.model.{Account, User}
import zio.{Task, URLayer, ZLayer}
import doobie.implicits._
import zio.interop.catz._


object UsersRepository {

  val live: URLayer[DbTransactor, UserRepository] = ZLayer.fromService {
    UsersRepository
  }

  trait Service {
    val resource: DbTransactor.Resource

    @deprecated
    def save(user: User): Task[Long]

    def getUser(idUser: User.Id): Task[User]

    def getUserByEmail(email: User.Email): Task[User]

    def updateAccount(idAccount: Account.Id, datetime: Account.LastConnexionDate, mdp:Option[Account.Password]): Task[Unit]

    def updateUser(idAccount: Account.Id, user:User): Task[Unit]

    def createAccount(user:User,password:Account.Password): Task[Long]

  }



  private[repository] final case class UsersRepository(resource: DbTransactor.Resource) extends Service with DBContext {
    import ctx._
    import resource._

    @deprecated
    override def save(user: User): Task[Long] = run(quote(nextUserId)).transact(xa) >>= saveUser(user)

    private def saveUser(candidat: User)(id: User.Id): Task[Long] =
      run(quote {
        userTable.insert(
          lift(
            candidat.copy(id=id)
          ))
      }).transact(xa)


    override def getUser(idUser: User.Id): Task[User] = ???

    override def getUserByEmail(email: User.Email): Task[User] = ???

    override def updateAccount(idAccount: Account.Id, datetime: Account.LastConnexionDate, mdp: Option[Account.Password]): Task[Unit] = ???

    override def updateUser(idAccount: Account.Id, user: User): Task[Unit] = ???

    override def createAccount(user: User, password: Account.Password): Task[Long] = ???
  }

}
