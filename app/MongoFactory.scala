import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.MongoClient

object MongoFactory {
  private val SERVER = System.getenv("DB_PATH")
  private val PORT = 27017
  private val DATABASE = "vazante"
  private val USERS = "users"
  private val PREFERRED_PATH = "preferred_path"

  val connection = MongoClient(SERVER)
  val user = connection(DATABASE)(USERS)
  val preferred = connection(DATABASE)(PREFERRED_PATH)
}
