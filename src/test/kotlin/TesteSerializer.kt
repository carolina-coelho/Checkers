package storage

import isel.tds.damas.model.board.Game
import isel.tds.damas.model.player.Player
import isel.tds.damas.view.isel.tds.damas.storage.GameSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class TesteSerializer {
    @Test
    fun `serialize Game with no Board`() {
        val game = Game(board = null, playerTurn = Player.B)
        val text = GameSerializer.serialize(game).trim()
        assertEquals("B", text, "O texto serializado deve apenas conter o turno do jogador b quando não há tabuleiro")
    }
}