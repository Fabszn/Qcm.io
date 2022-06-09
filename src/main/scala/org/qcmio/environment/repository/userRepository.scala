package org.qcmio.environment.repository

import org.qcmio.environment.domain.model.{Account, User}
import zio._

import javax.sql.DataSource

object userRepository {


  val layer: URLayer[Has[DataSource], Has[UserRepository]] =
    (UsersRepository(_)).toLayer


  trait UserRepository {
    @deprecated
    def save(user: User): Task[Long]

    def getUser(idUser: User.Id): Task[User]

    def getUserByEmail(email: User.Email): Task[User]

    def updateAccount(idAccount: Account.Id, datetime: Account.LastConnexionDate, mdp: Option[Account.Password]): Task[Unit]

    def updateUser(idAccount: Account.Id, user: User): Task[Unit]

    def createAccount(user: User, password: Account.Password): Task[Long]

  }


  private[repository] final case class UsersRepository(dataSource: DataSource) extends UserRepository {

    import QuillContext._

    val env = Has(dataSource)

    @deprecated
    override def save(user: User): Task[Long] = run(quote(nextUserId)).provide(env) >>= saveUser(user)

    private def saveUser(candidat: User)(id: User.Id): Task[Long] =
      run(quote {
        userTable.insert(
          _.id -> lift(id),
          _.nom -> lift(candidat.nom),
          _.prenom -> lift(candidat.prenom),
          _.email -> lift(candidat.email)
        )
      }).provide(env)


    override def getUser(idUser: User.Id): Task[User] = ???

    override def getUserByEmail(email: User.Email): Task[User] = ???

    override def updateAccount(idAccount: Account.Id, datetime: Account.LastConnexionDate, mdp: Option[Account.Password]): Task[Unit] = ???

    override def updateUser(idAccount: Account.Id, user: User): Task[Unit] = ???

    override def createAccount(user: User, password: Account.Password): Task[Long] = ???
  }

}
