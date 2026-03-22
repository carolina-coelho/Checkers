package isel.tds.damas.storage

import com.mongodb.ConnectionString
import com.mongodb.MongoClientException
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoCollection
import com.mongodb.kotlin.client.MongoDatabase
import java.io.Closeable

/**
 * Name of the environment variable that contains the connection string in format:
 *   mongodb+srv://<username>:<password>@<host>/[<database>][?<options>]
 * Example: MONGO_CONNECTION=mongodb+srv://palex:tds123abc@cluster0.brsewd2.mongodb.net/reversi?retryWrites=true&w=majority
 */

private const val ENV_CONNECTION = "MONGO_CONNECTION"
/**
 * Represents the MongoDB driver. Must be closed at the end.
 * The connection string of the environment variable is used to connect to the remote database,
 * The database name is defined in the constructor parameter or in the connection string.
 * @param nameDb Database name (override database name in connection string)
 */
class MongoDriver(nameDb: String? =null) : Closeable {
    val db: MongoDatabase
    private val client: MongoClient
    init {
        val envConnection = System.getenv(ENV_CONNECTION)
            ?: throw MongoClientException("Connection string in environment variable $ENV_CONNECTION is required")
        val dbName = requireNotNull(
            nameDb ?: ConnectionString(envConnection).database
        ) { "Database name required in constructor or in connection string" }
        client = MongoClient.create(envConnection)
        db = client.getDatabase(dbName)
    }
    override fun close() = client.close()
}


class Collection<T: Any>(val collection: MongoCollection<T>)


inline fun <reified T: Any> MongoDriver.getCollection(id: String) =
    Collection(db.getCollection(id, T::class.java))


inline fun <reified T: Any> MongoDriver.getAllCollections() =
    db.listCollectionNames().toList().map { getCollection<T>(it) }


fun <T: Any> Collection<T>.getAllDocuments(): List<T> =
    collection.find().toList()


fun <T: Any, K> Collection<T>.getDocument(id: K): T? =
    collection.find(Filters.eq(id)).firstOrNull()


fun <T: Any> Collection<T>.insertDocument(doc: T): Boolean =
    collection.insertOne(doc).insertedId!=null


fun <T: Any, K> Collection<T>.replaceDocument(id: K, doc: T): Boolean =
    collection.replaceOne(Filters.eq(id),doc).modifiedCount==1L


fun <T: Any, K> Collection<T>.deleteDocument(id: K): Boolean =
    collection.deleteOne(Filters.eq("_id",id)).deletedCount==1L


fun <T: Any> Collection<T>.deleteAllDocuments(): Boolean =
    collection.deleteMany(Filters.exists("_id")).wasAcknowledged()