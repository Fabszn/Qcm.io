import org.scalatest.wordspec.AnyWordSpec
import org.qcmio.model.{HttpReponse, Question, Reponse}
import io.circe.parser._
import io.circe.syntax._

class JsonTest extends AnyWordSpec{


    "Json should mapping" in {
      val jsonString = """{
  "id" : null,
  "idQuestion" : {
    "value" : 1
  },
  "label" : {
    "value" : "erze"
  },
  "isCorrect" : {
    "value" : true
  }
}""".stripMargin

      parse(jsonString)
        .fold(fail(_), v => {
          println(v)
          assert(v.as[HttpReponse].isRight)})

    }

  "Json should generate Json" in {

val hr = HttpReponse(id=None, idQuestion = Question.Id(1),label=Reponse.Label("erze"), isCorrect = Reponse.IsCorrect(true))

    println(hr.asJson)
    }
}
