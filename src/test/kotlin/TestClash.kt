
import isel.tds.damas.model.Game
import isel.tds.damas.storage.Storage
/*
class TestClash {

    // Simulação de um Storage para armazenar e recuperar o estado do jogo
    class MockStorage : Storage<Id, Game> {
        private val storage = mutableMapOf<Id, Game>()

        override fun create(id: Id, value: Game) {
            storage[id] = value
        }

        override fun read(id: Id): Game? {
            return storage[id]
        }

        override fun update(id: Id, value: Game) {
            storage[id] = value
        }

        override fun delete(id: Id) {
            storage.remove(id)
        }
    }

    // validação de ids
    @Test
    fun `Test Valid and Invalid Ids`() {
        // Testa um Id válido
        val validId = Id("Game123")
        assertEquals("Game123", validId.toString(), "O Id válido deve ser aceito")

        // Testa um Id inválido (vazio)
        assertFailsWith<IllegalArgumentException> { Id("") }
        // Testa um Id inválido (contém espaço)
        assertFailsWith<IllegalArgumentException> { Id("Game 123") }
        // Testa um Id inválido (contém caractere especial)
        assertFailsWith<IllegalArgumentException> { Id("Game#123") }
    }

}

 */