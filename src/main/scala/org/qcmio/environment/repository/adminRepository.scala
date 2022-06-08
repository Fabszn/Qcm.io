package org.qcmio.environment.repository


import org.qcmio.environment.domain.model.{Account, User}
import zio._

import javax.sql.DataSource

object adminRepository {


  val layer: URLayer[Has[DataSource], Has[AdminRepository]] = (AdminRepositoryService(_)).toLayer

  trait AdminRepository {

    def authUser(login: String, mdp: String): Task[Option[User]]

  }

  private[repository] final case class AdminRepositoryService(dataSource: DataSource) extends AdminRepository {

    import QuillContext._

    val env = Has(dataSource)

    override def authUser(login: String, mdp: String): Task[Option[User]] = run(quote(
      (userTable join accountTable on ((u: User, account: Account) => u.id == account.idUser))
        .filter {
          case (u, account) => u.email == lift(User.Email(login)) && account.mdp == lift(Account.Password(mdp))
        }.map(_._1)
    )).map(_.headOption).provide(env)
  }

  object admin {
    def authUser(login: String, mdp: String): RIO[Has[AdminRepository], Option[User]] =
      RIO.accessM(_.get.authUser(login: String, mdp: String))
  }
}

