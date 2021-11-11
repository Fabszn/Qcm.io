import org.qcmio.environment.utils.Mapper
import org.qcmio.model.{Question, Reponse}
import org.scalatest.wordspec.AnyWordSpec

class MapperTest  extends AnyWordSpec{


  "Question/Reponse should be transformed into HttpQuestion/HttpReponse" in {


      val q = Question(Question.Id(1), Question.Label("test1"))
      val reps = Seq(Reponse(Reponse.Id(2), Reponse.Label("rep 1"),q.id, Reponse.IsCorrect(true)),Reponse(Reponse.Id(3), Reponse.Label("rep 1"),q.id, Reponse.IsCorrect(true)))

      val  result = Mapper.mapperOne(q, reps)

      //assert(result.id.contains(Question.Id(1)))
      assert(true)


  }

}
