package isel.tds.damas.model.board.elements

import isel.tds.damas.model.board.*
import isel.tds.damas.model.player.Player

fun BoardRun.limitTheBoardWhenQueen(sFrom: Square, sTo: Square): Boolean {
    if (sFrom.row.index == 0 && this.turn == Player.W) {
        val sToValidPlayRow = sFrom.row.index + 1
        return sToValidPlayRow == sTo.row.index
    } else if (sFrom.row.index == BOARD_SIZE-1 && this.turn == Player.B) {
        val sToValidPlayRow = sFrom.row.index - 1
        return sToValidPlayRow == sTo.row.index
    }
    return true
}

fun toString(player: Player) = if (player == Player.B) "W" else "B"

fun BoardRun.becameQueen(sTo: Square): Boolean {
    return when {
        this.turn == Player.W && sTo.row.index == UPPER_ROW -> true
        this.turn == Player.B && sTo.row.index == BOTTOM_ROW -> true
        else -> false
    }
}
