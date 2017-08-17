package models
import com.mongodb.casbah.Imports._
import models.MongoFactory
import  play.api.libs.json._
import lib._
import org.bson.types.ObjectId

case class LocationInfo(user_id: String, time: String, coords: List[Coordenate], name: String, gender: String, picture: String) {}

object LocationInfo {
  def build(location: LocationInfo): MongoDBObject = {
    // create a builder
    val builder = MongoDBObject.newBuilder

    // user specificy data
    builder += "user_id" -> location.user_id
    builder += "name" -> location.name
    builder += "gender" -> location.gender
    builder += "picture" -> location.picture
    // time of leaving
    builder += "time" -> location.time
    
    // since coords is a array with nested properties
    // we need to build it from a List
    val coordsBuilder = MongoDBList.newBuilder

    // foreach coords create a object and build it 
    location.coords.foreach { coordenate =>
      val tmp_builder = MongoDBObject.newBuilder
      tmp_builder += "lat" -> coordenate.lat
      tmp_builder += "lng" -> coordenate.lng
      coordsBuilder += tmp_builder.result
    }

    // puts the coords
    builder += "coords" -> coordsBuilder.result
    builder.result
  }

  def save(location: LocationInfo) = {
    val mongoObj = build(location)
    MongoFactory.location.insert(mongoObj)
    // we can return because casbah does in place
    // i.e: it updates mongoObj
    mongoObj
  }

  def findOne(query: MongoDBObject) = {
    val result = MongoFactory.location.findOne(query)
    result match {
      case Some(x) => x
      case None => null
    }
  }

  def findOthers(location_id: String, user_id: String) = {
    val id = new ObjectId(location_id)
    // gets now
    val timestamp: Long = System.currentTimeMillis
    // get bounderies of 15 minutes before and 15 minutes after
    // from now
    val lowerBoundery = (timestamp - (60 * 60 * 1000)).toString
    val higherBoundery = (timestamp + (15 * 60 * 1000)).toString

    // query all locations that are inside the boundary
    // and location does not match our current location
    // neither our current id
    val q = ("_id" $ne id) ++ ("time" $gt lowerBoundery $lt higherBoundery) ++ ("user_id" $ne user_id)
    val result = MongoFactory.location.find(q)
    result
  }
}