package models
import com.mongodb.casbah.Imports._
import models.MongoFactory
import  play.api.libs.json._
import lib._
import org.bson.types.ObjectId

case class LocationInfo(user_id: String, time: String, coords: List[Coordenate]) {}

object LocationInfo {
  def build(location: LocationInfo): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "user_id" -> location.user_id
    builder += "time" -> location.time
    builder += "coords" -> location.coords
    builder.result
  }

  def save(location: LocationInfo) = {
    val mongoObj = build(location)
    MongoFactory.location.insert(mongoObj)

    mongoObj
  }

  def findOne(query: MongoDBObject) = {
    val result = MongoFactory.location.findOne(query)
    result match {
      case Some(x) => x
      case None => null
    }
  }

  def findOthers(location_id: String) = {
    val id = new ObjectId(location_id)
    // convert to minutes
    val timestamp: Long = System.currentTimeMillis
    // get bounderies of 15 minutes before and 15 minutes after
    // from now
    val lowerBoundery = (timestamp - (30 * 60 * 1000)).toString
    val higherBoundery = (timestamp + (15 * 60 * 1000)).toString

    // query all locations that are inside the boundary
    // and location does not match our current location
    val q = ("_id" $ne id) ++ ("time" $gt lowerBoundery $lt higherBoundery)
    val result = MongoFactory.location
      .find(q)
    result
  }
}