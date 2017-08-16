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

object Location extends Controller {
  /*
    This route is responsible for creating a new Location.
    It creates in the Database a instance Location with the user_id, time
    and the coords of the path the user takes to go home.

    It retuns the location_id of the recently created object.
  */
  def create = Action { request => 
    request.body.asJson.map { json =>

      implicit val coordsReader = Json.reads[Coordenate] 

      // extract json data sent
      val user_id = (json \ "user_id").as[JsString].value
      val time = (json \ "time").as[JsString].value
      // transform list of sent coords using implicit convertion
      val coords = (json \ "coords").asOpt[List[Coordenate]].getOrElse(List())
      
      // create a new location and save
      val new_location = LocationInfo(user_id, time, coords)
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

      val myCoordenates = location.as[BasicDBList]("coords").toList
      
      // find others locations
      val others = LocationInfo.findOthers(location_id)

      calculateIntersection(myCoordenates, others)

      Ok(Json.toJson("ok")).withHeaders(
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

  def calculateIntersection(main: List[Any], others: MongoCursor) = {
    var map = Map[String, Boolean]()

    (main.map (_.toString.replace("[", "").replace("]", "").trim.split(",")).toArray)
      .foreach(item => map = map + (item(0) + "@@" + item(1) -> true))

    others.foreach { each => {
        val coords = each.as[BasicDBList]("coords")
        println(each)
      }
    }
  }


//   function calculateIntersection(main, others) {
//   let hash = {}
  
//   main.forEach(function(path){
//     hash[path] = true
//   })
  
//   return others.map(function(map) {
//     return map.reduce(function(acc, item) {
//       if(hash[item] != null) return acc + 1
//       return acc
//     }, 0) * 100 / main.length
//   })
// }
}