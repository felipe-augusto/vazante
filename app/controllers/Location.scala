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
  */
  def matcher = Action { request => 
    request.body.asJson.map { json =>

      // extract json data sent
      val location_id = (json \ "location_id").as[JsString].value

      // find the location
      val location = LocationInfo.findOne(MongoDBObject("_id" -> new ObjectId(location_id)))
      val myCoordenates = location.as[BasicDBList]("coords")
      val user_id = location.as[String]("user_id")

      // find others locations
      val others = LocationInfo.findOthers(location_id, user_id)

      Ok(Json.toJson(calculateIntersection(myCoordenates, others).toList)).withHeaders(
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

        val coords = other.as[BasicDBList]("coords")
        
        // this is done in parallel two
        val similatiry = coords.par.foldLeft(0) { (acc, coordenate) =>
          val lat = coordenate.asInstanceOf[BasicDBObject].as[Double]("lat")
          val lng = coordenate.asInstanceOf[BasicDBObject].as[Double]("lng")
          val key = lat.toString + lng.toString
          if (map.getOrElse(key, false)) acc + 1
          else acc
        } * 100 / coords.size.toFloat

        Similarity(similatiry, name, gender, picture)
      }
    }
  }
}