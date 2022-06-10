package org.qcmio.environment.repository


import org.qcmio.environment.domain.auth.AuthenticatedUser
import zio._

import javax.sql.DataSource

object adminRepository {


  val layer: URLayer[Has[DataSource], Has[AdminRepository]] = (AdminRepositoryService(_)).toLayer

  trait AdminRepository {

    def authUser(login: String, mdp: String): Task[AuthenticatedUser]

  }

  private[repository] final case class AdminRepositoryService(dataSource: DataSource) extends AdminRepository {

    val env = Has(dataSource)

    override def authUser(login: String, mdp: String): Task[AuthenticatedUser] = Task.succeed(AuthenticatedUser(token = "", login = "")) /*run(quote(
      (userTable join accountTable on ((u: User, account: Account) => u.id == account.idUser))
        .filter {
          case (u, account) => u.email == lift(User.Email(login)) && account.mdp == lift(Account.Password(mdp))
        }.map(_._1)
    )).map(_.headOption).provide(env)*/
  }

  object admin {
    def authUser(login: String, mdp: String): RIO[Has[AdminRepository], AuthenticatedUser] =
      RIO.accessM(_.get.authUser(login: String, mdp: String))
  }
}

