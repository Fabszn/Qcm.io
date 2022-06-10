import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.qcmio.model
import org.qcmio.model.{HttpQuestion, Question, Reponse}
import org.scalatest.wordspec.AnyWordSpec

class JsonTest extends AnyWordSpec {


  "Json should mapping" in {
    val jsonString =
      """{
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
        assert(v.as[model.HttpReponse].isRight)
      })

  }

  "Json should generate Json" in {

    val hr = model.HttpReponse(id = None, idQuestion = Question.Id(1), label = Reponse.Label("erze"), isCorrect = Reponse.IsCorrect(true))

    println(hr.asJson)
  }


  "String should be mapped on HttpQuestion" in {
    val stringJson = """[{"id":{"value":1},"label":{"value":"question 1"},"reponses":[{"id":{"value":1},"idQuestion":{"value":1},"label":{"value":"fabrices"},"isCorrect":{"value":true}},{"id":{"value":2},"idQuestion":{"value":1},"label":{"value":"fabrices2"},"isCorrect":{"value":true}},{"id":{"value":3},"idQuestion":{"value":1},"label":{"value":"fabrices3"},"isCorrect":{"value":true}}]}]"""

    val r = parse(stringJson).fold(fail(_), v => {
      println(v)
      assert(v.as[Seq[HttpQuestion]].isRight)
    })

  }
}
