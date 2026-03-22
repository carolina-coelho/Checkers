package isel.tds.damas.model.board.elements

import isel.tds.damas.model.board.*
import isel.tds.damas.model.player.Player

const val BOARD_SIZE_8 = 8
const val N_PIECES_BOARD_SIZE_8 = 12

const val BOARD_SIZE_6 = 6
const val N_PIECES_BOARD_SIZE_6 = 6

const val BOARD_SIZE_4 = 4
const val N_PIECES_BOARD_SIZE_4 = 2

const val UPPER_ROW = BOARD_SIZE % BOARD_SIZE
const val BOTTOM_ROW = (BOARD_SIZE - 1) % BOARD_SIZE

open class Piece(player: Player?, val isQueen: Boolean) {
    val color: Player = if (player?.other == Player.B) Player.W else Player.B

    companion object {
        val numberOfBlackPieces = numbOfPeaces(BOARD_SIZE) // Corrected function name
        val numberOfWhitePieces = numbOfPeaces(BOARD_SIZE) // Corrected function name
    }
}

fun numbOfPeaces(boardSize: Int): Int? { //RETORNA o numero de PEÇAS CONFORME O TAMANHO DO BOARD
    return when (boardSize) {
        BOARD_SIZE_8 -> N_PIECES_BOARD_SIZE_8
        BOARD_SIZE_6 -> N_PIECES_BOARD_SIZE_6
        BOARD_SIZE_4 -> N_PIECES_BOARD_SIZE_4
        else -> null
    }
}


