package org.qcmio.environment.repository

import cats.implicits.catsSyntaxFlatMapOps
import org.qcmio.model.{Account, Candidat}
import zio.{RIO, Task, URLayer, ZLayer}
import doobie.implicits._

object CandidatsRepository {

  val live: URLayer[DbTransactor, CandidatRepository] = ZLayer.fromService {
    CandidatsRepository
  }

  trait Service {
    val resource: DbTransactor.Resource

    def save(candidat: Candidat): RIO[CandidatRepository, Long]

    def getCandidat(idCandidat: Candidat.Id): RIO[CandidatRepository, Candidat]

    def getCandidatByEmail(email: Candidat.Email): RIO[CandidatRepository, Candidat]

    def updateAccount(idAccount: Account.Id, datetime: Account.LastConnexionDate): RIO[CandidatRepository, Unit]

    def createAccount(account: Account): RIO[CandidatRepository, Long]

    def createCandidat(candidat: Candidat): RIO[CandidatRepository, Long]


  }

  object candidat {

  }

  private[repository] final case class CandidatsRepository(resource: DbTransactor.Resource) extends Service with DBContext {
    import ctx._
    import resource._

    override def save(candidat: Candidat): RIO[CandidatRepository, Long] = run(quote(nextCandidatId)) >>= saveCandidat(candidat)

    private def saveCandidat(candidat: Candidat)(id: Candidat.Id): Task[Long] =
      run(quote {
        candidatTable.insert(
          lift(
            candidat.copy(id=id)
          ))
      }).transact(xa)


    override def getCandidat(idCandidat: Candidat.Id): RIO[CandidatRepository, Candidat] = ???

    override def getCandidatByEmail(email: Candidat.Email): RIO[CandidatRepository, Candidat] = ???

    override def updateAccount(idAccount: Account.Id, datetime: Account.LastConnexionDate): RIO[CandidatRepository, Unit] = ???

    override def createAccount(account: Account): RIO[CandidatRepository, Long] = ???

    override def createCandidat(candidat: Candidat): RIO[CandidatRepository, Long] = ???
  }

}
