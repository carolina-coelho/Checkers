package model.board.elements

import isel.tds.damas.model.board.*
import isel.tds.damas.model.board.elements.*
import isel.tds.damas.model.board.elements.Square
import isel.tds.damas.model.player.Player
import kotlin.test.*

class TestPiece {

    // verificação das peças correspondentes aos jogadores no tabuleiro inicial
    @Test
    fun `Test Piece Creation for Players`() {
        val square = Square(2, 3)

        // Testa a criação de uma peça para o jogador Player.b
        val blackPiece = Piece(Player.B, isQueen = false)
        assertEquals(Player.B, blackPiece.color, "Peça deveria ser preta para Player.b")
        assertFalse(blackPiece.isQueen, "Peça inicialmente não deveria ser uma rainha")

        // Testa a criação de uma peça para o jogador Player.w
        val whitePiece = Piece(Player.W, isQueen = false)
        assertEquals(Player.W, whitePiece.color, "Peça deveria ser branca para Player.w")
        assertFalse(whitePiece.isQueen, "Peça inicialmente não deveria ser uma rainha")
    }

    // testar se para 12 peças o tamanho do tabuleiro, etc..
    @Test
    fun `Test Number of Pieces Based on Board Size`() {
        assertEquals(12, numbOfPeaces(8), "Para tabuleiros de 8x8, o número de peças deveria ser 12")
        assertEquals(6, numbOfPeaces(6), "Para tabuleiros de 6x6, o número de peças deveria ser 6")
        assertEquals(2, numbOfPeaces(4), "Para tabuleiros de 4x4, o número de peças deveria ser 2")
        assertNull(numbOfPeaces(10), "Para tamanhos de tabuleiro não suportados, deve retornar null")
    }
    // verificar se o tornar rainha funciona
    @Test
    fun `Test Queen Promotion Functionality`() {
        val board = BoardRun(mapOf(), Player.W)
        // Simula a promoção para uma peça já existente como rainha
        val pieceToPromote = Piece( Player.W, isQueen = false)
        val promotedPiece = Piece( pieceToPromote.color, isQueen = true)

        assertTrue(promotedPiece.isQueen, "Peça promovida deve ser uma rainha")
        assertEquals(pieceToPromote.color, promotedPiece.color, "A cor da peça promovida deve permanecer a mesma")
    }
}
