package isel.tds.damas.storage

import java.nio.file.Path
import kotlin.io.path.*

class TextFileStorage<Key, Data>(
    baseFolderName: String,
    private val serializer: Serializer<Data>
) : Storage<Key, Data> {
    private val basePath = Path(baseFolderName)

    init {  // Cria o caminho para o ficheiro se não existir
        with(basePath) {
            if (!exists()) createDirectory()
            else check(isDirectory()) { "$name is not a directory" }
        }
    }

    /*
     * inline, evita overheads de chamada, assim fx é diretamente embutido
     * onde a função é chamada. Melhorando assim o desempenho
     *
     * constroi o caminho completo para o ficheiro associado em Key, daí conectar
     * basePath com o key.txt, por fim aplica-se a funçao fx()
     *
     * fx, é uma função em que nesta será chamada em um path, e é aoperação a
     * ser executada no caminho do ficheiro gerado a partir de key.
     * */
    private inline fun <Data> with(key: Key, fx: Path.() -> Data): Data =
        (basePath / "$key.txt").fx()

    /*
    * criação de um novo ficheiro para armazenar os dados a uma key especifica
    * os dados(data) é escrito após ser serializado, caso um ficheiro com uma
    * key já existente lança erro
    * */
    override fun create(key: Key, data: Data) = with(key) {
        check(!exists()) { "File $key exists" }
        writeText(serializer.serialize(data))
    }

    /*
    * lê o conteudo e deserializa-o para obter o dado(data) original
    * caso o ficheiro não exista retorna null
    * */
    override fun read(key: Key): Data? = with(key) {
        try {
            serializer.deserialize(readText())
        } catch (e: NoSuchFileException) {
            null
        }
    }

    /*
    * atualizar os dados, caso o ficheiro com aquela key já exista
    *  */
    override fun update(key: Key, data: Data) = with(key) {
        check(exists()) { "File $key exists" }
        writeText(serializer.serialize(data))
    }

    /*
    * apaga o ficheiro caso exista, ou seja, houver key
    * */
    override fun delete(key: Key) = with(key) {
        check(deleteIfExists()) { "File $key doesn't exist" }
    }
}