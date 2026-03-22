import isel.tds.damas.model.board.*
import isel.tds.damas.model.board.elements.*
import isel.tds.damas.model.player.Player
/*


/*
* Função fold é uma função de agregação que permite transformar uma coleção
* num único valor, aplicando repetidamente uma função de transformação.
* Logo, fold é usada para simular uma sequência de jogadas
*
* playSequence-> função auxiliar para simular uma sequência de movimentos no jogo
* */

fun playSequence(vararg moves: String, board: Board): Board =
    moves.fold(board) { b, idx ->
        idx.split(" ").let { (pos, suffix) ->
            val from = pos.toSquare()
            val to = suffix.toSquare()
            b.play(from, to)
        }
    }

class TestBoard {

    /*Iniciação do tabuleiro
    *verificação se o tabuleiro tem 12 peças em cada jogador
    *verificação se o jogador com o primeiro turno é branco (w)
    * */
    @Test
    fun `Test initial board`() {
        val board = startGame()

        //peças dos jogadores
        val whitePiecesCount = board.moves.values.count { it.color == Player.W }
        val blackPiecesCount = board.moves.values.count { it.color == Player.B }

        assertEquals(12, whitePiecesCount, "O tabuleiro inicial deve conter 12 peças para o jogador branco")
        assertEquals(12, blackPiecesCount, "O tabuleiro inicial deve conter 12 peças para o jogador preto")
    }

    // comer em duplas
    @Test
    fun `Test eat twice in the same turn`() {
        val board = startGame()

        val newBoard = playSequence(
            "3a 4b w", "6f 5e b", "2b 3a w", "7e 6f b", "1c 2b w", "8f 7e b", "3g 4h w", "6d 5c b",
            "4b 6d w", board = board
        )

        assertTrue(newBoard.moves.containsKey(Square(0, 5)), "A peça branca deve estar na posição 8f após comer")
        assertFalse(
            newBoard.moves.containsKey(Square(3, 2)),
            "As peças pretas comidas devem ser removidas do tabuleiro"
        )
        assertFalse(
            newBoard.moves.containsKey(Square(1, 4)),
            "As peças pretas comidas devem ser removidas do tabuleiro"
        )

    }

    // teste para comer uma peça
    @Test
    fun `Test eat piece`() {
        val board = startGame()

        val newBoard = playSequence("3a 4b w", "6d 5c b", "4b 6d w", board = board)

        assertTrue(newBoard.moves.containsKey(Square(2, 3)), "A peça branca deve estar na posição 6d após comer")
        assertFalse(newBoard.moves.containsKey(Square(3, 2)), "A peça preta comida deve ser removida do tabuleiro")
    }

    //teste para que se torne rainha white
    @Test
    fun `Test become a Queen`() {
        val board = startGame()

        val newBoard = playSequence(
            "3a 4b w", "6f 5e b", "2b 3a w", "7e 6f b", "1c 2b w", "8f 7e b", "3g 4h w", "6d 5c b",
            "4b 6d w", board = board
        )

        val promotePiece = newBoard.moves[Square(0, 5)]       //8f
        assertTrue(newBoard.moves.containsKey(Square(0, 5)), "A peça branca deve estar na posição 8f após comer")
        assertTrue(promotePiece?.isQueen == true, "A peça deve ser promovida a rainha quando atinge a última linha")
    }

    // teste para tentar mover para trás quaso seja uma peça normal
    @Test
    fun `Test Invalid Move Backwards`() {
        // Cria o tabuleiro inicial
        val board = startGame()

        // Tenta realizar o movimento válido
        val newBoard = assertFailsWith<IllegalStateException> {
            playSequence("3a 4b w", "4b 3a w", board = board)
        }
        assertEquals("Not your turn.", newBoard.message)
    }

    // Teste para verificar se um jogador é declarado vencedor quando o oponente não tem peças
    @Test
    fun `Test Declare Winner`() {
        // Simula um tabuleiro onde apenas uma peça branca resta
        val board = BoardRun(mapOf(Square(7, 0) to Piece(Player.W, false)), Player.W)


        val winnerBoard = board.play(Square(7, 0), Square(6, 1))

        assertTrue(winnerBoard is BoardWin, "O jogo deve declarar um vencedor se o oponente não tem mais peças")
    }

    // teste para verificação de movimento obrigatório de captura
    @Test
    fun `Test Mandatory Capture Move`() {
        val board = startGame()

        val newBoard = playSequence(
            "3a 4b w", "6f 5e b", "2b 3a w", "7e 6f b", "1c 2b w", "8f 7e b", "3g 4h w", "6d 5c b", board = board
        )

        val ex = assertFailsWith<IllegalStateException> {
            playSequence("4b 5a w", board = newBoard)
        }
        assertEquals("There is a mandatory piece to eat.", ex.message)
    }

    @Test
    fun `Test move with queen`() {
        val board = startGame()

        val newBoard = playSequence(
            "3a 4b w", "6f 5e b", "2b 3a w", "7e 6f b", "1c 2b w", "8f 7e b", "3g 4h w", "6d 5c b",
            "4b 6d w", "6b 5a b", "8f 7e W", "7c 6d b", "7e 5c W", board = board
        )

        assertTrue(newBoard.moves.containsKey(Square(3, 2)), "A peça queen deve estar na posição 5c após comer")
        assertFalse(newBoard.moves.containsKey(Square(2, 3)), "A peça preta comida deve ser removida do tabuleiro")
    }

    @Test
    fun  `Test Mandatory Capture Move With Queen`(){
        val board = startGame()

        val newBoard = playSequence(
            "3a 4b w", "6h 5g b", "2b 3a w", "5g 4f b", "3e 5g w", "6f 4h b", "2d 3e w", "7g 6h b",
            "3e 4f w", "6d 5e b", "4f 6d w", "7c 5e b", "1e 2d w", "8b 7c b", "2d 3e w", "7c 6d b",
            "1a 2b w", "8f 7g b", "1c 2d w", "8d 7c b", "3g 4f w", "5e 3g b", "3c 4d w",
            board = board
        )

        val ex = assertFailsWith<IllegalStateException> {
            playSequence("7g 6f b", board = newBoard)
        }

        assertEquals("There is a mandatory piece to eat.", ex.message)
    }

    @Test fun `Test Multiple Capture With Queen`(){
        val board = startGame()

        val newBoard= playSequence(
            "3a 4b w",
            "6h 5g b",
            "2b 3a w",
            "5g 4f b",
            "3e 5g w",
            "6f 4h b",
            "2d 3e w",
            "7g 6h b",
            "3e 4f w",
            "6d 5e b",
            "4f 6d w",
            "7c 5e b",
            "1e 2d w",
            "8b 7c b",
            "2d 3e w",
            "7c 6d b",
            "1a 2b w",
            "8f 7g b",
            "1c 2d w",
            "8d 7c b",
            "3g 4f w",
            "5e 3g b",
            "3c 4d w",
            "1e 3c B",
            board = board
        )

        assertTrue(newBoard.moves.containsKey(Square(7, 0)), "A peça preta deve estar na posição 1a após comer")
        assertFalse(newBoard.moves.containsKey(Square(6,3)), "A peça branca 2d comida deve ser removida do tabuleiro")
        assertFalse(newBoard.moves.containsKey(Square(6,1)), "A peça branca 2b comida deve ser removida do tabuleiro")
    }

    @Test fun `Test piece b eats Queen W`(){
        val board = startGame()
        val newBoard = playSequence(
            "3a 4b w",
            "6h 5g b",
            "2b 3a w",
            "5g 4f b",
            "3e 5g w",
            "6f 4h b",
            "2d 3e w",
            "7g 6h b",
            "3e 4f w",
            "6d 5e b",
            "4f 6d w",
            "7c 5e b",
            "1e 2d w",
            "8b 7c b",
            "2d 3e w",
            "7c 6d b",
            "1a 2b w",
            "8f 7g b",
            "1c 2d w",
            "8d 7c b",
            "3g 4f w",
            "5e 3g b",
            "3c 4d w",
            "1e 3c B",
            "2h 3g w",
            "4h 2f b",
            "3e 4f w",
            "7g 6f b",
            "1g 3e w",
            "8h 7g b",
            "4d 5c w",
            "6b 4d b",
            "4b 5c w",
            "6d 4b b",
            "3a 5c w",
            "6h 5g b",
            "4f 6h w",
            "1a 2b B",
            "8f 6d W",
            "2b 3a B",
            "5c 6b w",
            "7a 5c b",
            "8b 7c W",
            "3a 4b B",
            "7c 6b W",
            "4b 5a B",
            board = board
        )
            assertTrue(newBoard.moves.containsKey(Square(3, 0)), "A peça preta deve estar na posição 5a após comer")
    }

    @Test fun `Test game over`(){
        val board = startGame()
        val newBoard = playSequence(
            "3a 4b w",
            "6h 5g b",
            "2b 3a w",
            "5g 4f b",
            "3e 5g w",
            "6f 4h b",
            "2d 3e w",
            "7g 6h b",
            "3e 4f w",
            "6d 5e b",
            "4f 6d w",
            "7c 5e b",
            "1e 2d w",
            "8b 7c b",
            "2d 3e w",
            "7c 6d b",
            "1a 2b w",
            "8f 7g b",
            "1c 2d w",
            "8d 7c b",
            "3g 4f w",
            "5e 3g b",
            "3c 4d w",
            "1e 3c B",
            "2h 3g w",
            "4h 2f b",
            "3e 4f w",
            "7g 6f b",
            "1g 3e w",
            "8h 7g b",
            "4d 5c w",
            "6b 4d b",
            "4b 5c w",
            "6d 4b b",
            "3a 5c w",
            "6h 5g b",
            "4f 6h w",
            "1a 2b B",
            "8f 6d W",
            "2b 3a B",
            "5c 6b w",
            "7a 5c b",
            "8b 7c W",
            "3a 4b B",
            "7c 6b W",
            "4b 5a B",
            "6b 4d W",
            "2f 1e b",
            "4d 3c W",
            "6f 5g b",
            "3c 4b W",
            "5a 3c B",
            board = board
        )

        assertTrue(newBoard.playerWithoutPieces() && newBoard is BoardWin)
        val exception = try {
            playSequence("4b 2d W", board = newBoard)
            null
        } catch (e: IllegalStateException) {
            e
        }

        assertNotNull(exception)
        assertEquals("Game over", exception.message)
    }

    @Test fun `Test game over 2`(){
        val board = emptyMap<Square,Piece>()
        val finalBoard = board
            .plus(Pair("7a".toSquare(),Piece(Player.B,false)))
            .plus(Pair("6b".toSquare(),Piece(Player.W,false)))
        val testBoard = BoardRun(finalBoard,Player.B)

        val newBoard = playSequence("7a 5c b", board = testBoard)

        assertTrue(newBoard.playerWithoutPieces() && newBoard is BoardWin)
        val exception = try {
            playSequence("4b 2d W", board = newBoard)
            null
        } catch (e: IllegalStateException) {
            e
        }

        assertNotNull(exception)
        assertEquals("Game over", exception.message)
    }

    @Test fun `Multiple eat options`(){
        val board = emptyMap<Square,Piece>()
        val finalBoard = board
            .plus(Pair("7a".toSquare(),Piece(Player.B,false)))
            .plus(Pair("6h".toSquare(),Piece(Player.B,false)))
            .plus(Pair("6b".toSquare(),Piece(Player.W,false)))
            .plus(Pair("5g".toSquare(),Piece(Player.W,false)))
            .plus(Pair("1a".toSquare(),Piece(Player.B,true)))
            .plus(Pair("2b".toSquare(),Piece(Player.W,false)))
            .plus(Pair("2f".toSquare(),Piece(Player.B,true)))
            .plus(Pair("3g".toSquare(),Piece(Player.W,false)))
        val testBoard = BoardRun(finalBoard,Player.B)

        val newBoard1 = playSequence("7a 5c b", board = testBoard)
        assertTrue { !newBoard1.moves.containsKey("6b".toSquare()) }
        assertTrue { newBoard1.moves.size == 7 }
        val newBoard2 = playSequence("6h 4f b", board = testBoard)
        assertTrue { !newBoard2.moves.containsKey("5g".toSquare()) }
        // Dupla captura
        assertTrue { newBoard2.moves.size == 6 }
        assertTrue { newBoard2.moves.containsKey("2h".toSquare()) }
        val newBoard3 = playSequence("1a 3c B", board = testBoard)
        assertTrue { !newBoard3.moves.containsKey("2b".toSquare()) }
        assertTrue { newBoard3.moves.size == 7 }
        val newBoard4 = playSequence("2f 4h B", board = testBoard)
        assertTrue { !newBoard4.moves.containsKey("3g".toSquare()) }
        assertTrue { newBoard4.moves.size == 7 }
    }
}

 */