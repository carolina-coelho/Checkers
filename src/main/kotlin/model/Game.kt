package isel.tds.damas.model

import isel.tds.damas.model.board.Board
import isel.tds.damas.model.board.elements.*
import isel.tds.damas.model.board.play
import isel.tds.damas.model.board.startGame
import isel.tds.damas.model.player.Player

data class Game(
    val board: Board? = null,
    val playerTurn: Player = Player.W,
)

fun Game.nextPlayer(): Player = if (this.playerTurn == Player.W) Player.B else Player.W

fun Game.start(): Game {
    val board = startGame()
    return Game(board, this.playerTurn)
}

fun Game.play(sFrom: Square?, sTo: Square?): Game {
    checkNotNull(board) { "Game not started" }
    checkNotNull(sFrom) { "A posição $sFrom é inválida" }
    checkNotNull(sTo) { "A posição $sTo não existe" }
    val board = board.play(sFrom, sTo)
    return copy(board = board, playerTurn = nextPlayer())
}
