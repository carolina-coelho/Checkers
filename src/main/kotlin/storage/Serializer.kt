package isel.tds.damas.storage

/* deserialize-> converte o texto de volta para um objeto Data.
* */
interface Serializer<Data> {
    fun serialize(data: Data): String
    fun deserialize(text: String): Data
}

