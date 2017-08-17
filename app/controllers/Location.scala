package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.json.Json

import play.api.db._
import models._
import lib._
import play.api.libs.json._

import scalaj.http._
import pdi.jwt.JwtSession

import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._

import scala.collection.parallel.immutable._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Location extends Controller {
  implicit val coordsReader = Json.reads[Coordenate] 
  implicit val similarityWrites = Json.writes[Similarity]
  /*
    This route is responsible for creating a new Location.
    It creates in the Database a instance Location with the
    user name, sex, picture; time of leaving
    and the coords of the path the user takes to go home.

    It retuns the location_id of the recently created object.

    {
      "location_id": "5995e3087ffca4467e8fa01b"
    }
  */
  def create = Action { request => 
    request.body.asJson.map { json =>

      // extract json data sent
      val user_id = (json \ "user_id").as[JsString].value
      val user = User.find(MongoDBObject("_id" -> new ObjectId(user_id)))

      // get info from user to save in location info
      // this avoids joins later
      val name = user.as[String]("name")
      val gender = user.as[String]("gender")
      val picture = user.as[String]("picture")

      val time = (json \ "time").as[JsString].value
      // transform list of sent coords using implicit convertion
      val coords = (json \ "coords").asOpt[List[Coordenate]].getOrElse(List())
      
      // create a new location and save
      val new_location = LocationInfo(user_id, time, coords, name, gender, picture)
      val saved = LocationInfo.save(new_location)

      // returns the ID of the recently created location
      val response = Map("location_id" -> saved._id.get.toString())

      Ok(Json.toJson(response)).withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
        "Access-Control-Allow-Headers" -> "Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With",
        "Access-Control-Allow-Credentials" -> "true",
        "Access-Control-Max-Age" -> (60 * 60 * 24).toString
      )
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }
  /*
    This route is responsible for returning a list of possible
    candidates for you to go home, for this to happen you must pass your
    location_id so that we know the path you take.

    It returns an ordered list of the possible matchs

    [
      {
        "percentage": 69.44444274902344,
        "name": "email3@t.com",
        "gender": "female",
        "picture": "https://randomuser.me/api/portraits/thumb/women/15.jpg",
        "time": "1502993386847"
      },
      {
        "percentage": 16,
        "name": "email3@t.com",
        "gender": "male",
        "picture": "https://randomuser.me/api/portraits/thumb/men/10.jpg",
        "time": "1502993386847"
      },
      {
        "percentage": 5,
        "name": "email3@t.com",
        "gender": "male",
        "picture": "https://randomuser.me/api/portraits/thumb/men/10.jpg",
        "time": "1502993386847"
      },
      {
        "percentage": 3.225806474685669,
        "name": "email2@t.com",
        "gender": "male",
        "picture": "https://randomuser.me/api/portraits/thumb/men/2.jpg",
        "time": "1502993386846"
      },
      {
        "percentage": 1.8867924213409424,
        "name": "email1@t.com",
        "gender": "female",
        "picture": "https://randomuser.me/api/portraits/thumb/women/1.jpg",
        "time": "1502993386844"
      }
    ]
  */
  def matcher = Action { request => 
    request.body.asJson.map { json =>

      // extract json data sent
      val location_id = (json \ "location_id").as[JsString].value

      // find the location
      val location = LocationInfo.findOne(MongoDBObject("_id" -> new ObjectId(location_id)))
      val myCoordenates = location.as[BasicDBList]("coords")
      val time = location.as[String]("time")
      val user_id = location.as[String]("user_id")

      // find others locations
      val others = LocationInfo.findOthers(location_id, user_id, time)

      // calculate route intersection with percentage
      val result = calculateIntersection(myCoordenates, others).toList

      // order the result
      val sorted = QuickSort.orderedTrait[Similarity](result)

      /*
        It is also possible to order using first class functions
        instead of the trait ordered:
        
        def comp(x: Similarity, y: Similarity) = x.percentage < y.percentage
        def equal(x: Similarity, y: Similarity) = x.percentage == y.percentage
        val sorted =  QuickSort.firstClass[Similarity](result, comp, equal)
      */

      Ok(Json.toJson(sorted)).withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
        "Access-Control-Allow-Headers" -> "Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With",
        "Access-Control-Allow-Credentials" -> "true",
        "Access-Control-Max-Age" -> (60 * 60 * 24).toString
      )
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  /*
    This function is responsible to compare one location with all the
    locations that are inside a time boundary.

    Since this methods uses concurrence, it return a ParSeq[Similarity]

  */

  def calculateIntersection(main: BasicDBList, others: MongoCursor) = {
    var map = Map[String, Boolean]()
    // compose a map
    main.foreach { coordenate => 
      val lat = coordenate.asInstanceOf[BasicDBObject].as[Double]("lat")
      val lng = coordenate.asInstanceOf[BasicDBObject].as[Double]("lng")
      map = map + ((lat.toString + lng.toString) -> true)
    }

    // execute in parallel for others locations
    others.toList.par.map { other => {
        val name = other.as[String]("name")
        val gender = other.as[String]("gender")
        val picture = other.as[String]("picture")
        val time = other.as[String]("time")

        val coords = other.as[BasicDBList]("coords")
        
        // this is done in parallel too
        val percentage = coords.par.foldLeft(0) { (acc, coordenate) =>
          val lat = coordenate.asInstanceOf[BasicDBObject].as[Double]("lat")
          val lng = coordenate.asInstanceOf[BasicDBObject].as[Double]("lng")
          val key = lat.toString + lng.toString
          if (map.getOrElse(key, false)) acc + 1
          else acc
        } * 100 / coords.size.toFloat

        Similarity(percentage, name, gender, picture, time)
      }
    }
  }
}