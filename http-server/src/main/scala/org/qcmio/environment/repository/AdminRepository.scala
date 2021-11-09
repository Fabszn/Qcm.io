package org.qcmio.environment.repository


import zio.{RIO, Task, URLayer, ZLayer}
import doobie.implicits._
import org.qcmio.model.{Account, User}
import zio.interop.catz._

object AdminRepository {


  val live: URLayer[DbTransactor, AdministratorRepository] = ZLayer.fromService {
    AdminRepository
  }


  trait Service {

    val resource: DbTransactor.Resource

    def authUser(login:String, mdp:String):Task[Option[User]]

  }

  private[repository] final case class AdminRepository(resource: DbTransactor.Resource) extends Service with DBContext {

    import ctx._
    import resource._
    override def authUser(login: String, mdp: String): Task[Option[User]] = run( quote(
      (userTable join accountTable on ((u:User,account:Account) => u.id == account.idUser))
        .filter{
          case (u,account) => u.email == lift(User.Email(login)) && account.mdp== lift(Account.Password(mdp))
        }.map(_._1)
        )).transact(xa).map(_.headOption)
      }

  object admin {
    def  authUser(login:String, mdp:String): RIO[AdministratorRepository, Option[User]] =
      RIO.accessM(_.get.authUser(login:String, mdp:String))
  }
}

