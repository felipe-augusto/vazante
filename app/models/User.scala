package models
import com.mongodb.casbah.Imports._
import models.MongoFactory

case class User(name: String, email: String, picture: String, gender: String, fb_id: String)

object User {
  def build(user: User): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "name" -> user.name
    builder += "email" -> user.email
    builder += "picture" -> user.picture
    builder += "gender" -> user.gender
    builder += "fb_id" -> user.fb_id
    builder.result
  }

  def save(user: User) = {
    val mongoObj = build(user)
    MongoFactory.user.save(mongoObj)
  }

  def find(query: MongoDBObject) = {
    val result = MongoFactory.user.findOne(query)
    result match {
      case Some(x) => x
      case None => null
    }
  }

  def mongoObjToUser(obj: MongoDBObject): User = {
    val name = obj.getAs[String]("name").get
    val email = obj.getAs[String]("email").get
    val picture = obj.getAs[String]("picture").get
    val gender = obj.getAs[String]("gender").get
    val fb_id = obj.getAs[String]("fb_id").get
    User(name, email, picture, gender, fb_id)
  }
}