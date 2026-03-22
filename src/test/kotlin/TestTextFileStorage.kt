package storage

import TextFileStorage
import isel.tds.damas.storage.Serializer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.*

class TestTextFileStorage {
    companion object {
        private const val FOLDER_NAME = "test"
        val storage = TextFileStorage<String,String>(FOLDER_NAME,
            object : Serializer<String> {
                override fun serialize(data: String) = data
                override fun deserialize(text: String) = text
            }
        )
        @BeforeAll
        @JvmStatic fun setup() {
            assertTrue(Path(FOLDER_NAME).exists())
        }
        @AfterAll
        @JvmStatic fun cleanup() {
            @OptIn(ExperimentalPathApi::class)
            Path(FOLDER_NAME).deleteRecursively()
        }
    }
    /*
    * Verificar se um ficheiro pode ser criado e lido corretamente
    * */
    @Test fun `Create and Read an entry`() {
        val key = "e1"
        storage.create(key,"content")
        assertEquals("content", storage.read(key))
    }
    /*
    * Verificar se um ficheiro existente pode ser atualizada (uptade)
    * */
    @Test fun `Create and Update an entry`() {
        val key = "e2"
        storage.create(key,"old content")
        storage.update(key,"new content")
        assertEquals("new content", storage.read(key))
    }
    /*
    * Verificar o comportamento ao tentar criar um ficheiro com um id igual
    * */
    @Test fun `Create an entry that already exists`() {
        val key = "e4"
        storage.create(key,"content")
        assertFailsWith<IllegalStateException> {
            storage.create(key,"content")
        }
    }
    /*
    * Verificar o comportamento ao tentar atualizar um ficheiro inexistente
    * */
    @Test fun `Update an entry that does not exist`() {
        assertFailsWith<IllegalStateException> {
            storage.update("e6","content")
        }
    }
    /*
    * Verificar o comportamento ao tentar excluir um ficheiro inexistente
    * */
    @Test fun `Delete an entry that does not exist`() {
        assertFailsWith<IllegalStateException> {
            storage.delete("e7")
        }
    }
}