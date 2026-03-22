/*

import isel.tds.damas.model.board.elements.Square

import isel.tds.damas.model.board.Game
import isel.tds.damas.model.board.play
import isel.tds.damas.model.board.start
import isel.tds.damas.model.player.Player
import kotlin.test.*

class TesteGame {

    // iniciação do jogo com o turno de white, game inicial
    @Test
    fun `Test Game Start`() {
        val game = Game().start()
        assertNotNull(game.board, "O jogo deve iniciar com um tabuleiro válido")
        assertEquals(Player.W, game.playerTurn, "O primeiro turno tem de ser o jogador white")

        // peças de cada jogador tem de ser 12
        val whiteInicialPiece = game.board!!.moves.values.count { it.color == Player.W }
        val blackInicialPiece = game.board!!.moves.values.count { it.color == Player.B }

        assertEquals(12, whiteInicialPiece, "O tabuleiro inicial deve conter 12 peças brancas")
        assertEquals(12, blackInicialPiece, "O tabuleiro inicial deve conter 12 peças pretas")
    }

    // turno tem de alternar corretamente após uma jogada
    @Test
    fun `Test Turn Alternation`() {
        val game = Game().start()

        val sFrom = Square(5, 0)  // Posição inicial de uma peça branca
        val sTo = Square(4, 1)    // Posição para onde a peça se moverá

        val gameAfterMove = game.play(sFrom, sTo)

        assertEquals(Player.B, gameAfterMove.playerTurn, "Após o primeiro movimento, o turno deve ser do jogador preto")
    }

    // tem de dar erro ao tentar fazer uma jogada antes de inicicar o jogo
    @Test
    fun `Test Play Without Starting Game`() {
        val game = Game() // Jogo não iniciado

        val sFrom = Square(5, 0)
        val sTo = Square(4, 1)

        val exception = assertFailsWith<IllegalStateException> {
            game.play(sFrom, sTo)
        }

        assertEquals("Game not started", exception.message, "Deve lançar um erro ao tentar jogar sem iniciar o jogo")
    }

}
 */