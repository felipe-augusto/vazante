package models
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.MongoConnection

object MongoFactory {
  private val SERVER = "localhost"
  private val PORT = 27017
  private val DATABASE = "vazante"
  private val USERS = "users"

  val connection = MongoConnection(SERVER)
  val user = connection(DATABASE)(USERS)
}
