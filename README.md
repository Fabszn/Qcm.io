# Purpose 

    //to describ

# Stack

## Backend
    Http4s (https://http4s.org)
    ZIO (https://zio.dev)
    Doobie (https://tpolecat.github.io/doobie/)
    Quill (https://getquill.io/)
    Chimney (https://github.com/scalalandio/chimney)
## Frontend   
    ScalaJs 
    Laminar (http://Laminar.dev/)
    AirStream (https://github.com/raquo/Airstream)
    waypoint
## Shared
    Circe

## Tools

    Postgres
    Docker

# Start app

    1 - DB 
        docker-compose up

    2 - App
        sbt http/run


# MVP 

first purpose is to enable to students to attend an exam.

To achieve this target, I need the following points :

 - Add an question
 - Add reponse to a question
 - add question  to an exam
 - add Student
 - add student to an exam
 - When user log in to app, Student can see its questions
 - Student can answer to one question 

 **All these features, implie both APIs and UI** 

