package isel.tds.damas.storage

import isel.tds.damas.model.board.Board
import isel.tds.damas.model.board.BoardRun
import isel.tds.damas.model.board.BoardWin
import isel.tds.damas.model.board.elements.toSquare
import isel.tds.damas.model.player.toPlayer



import isel.tds.damas.model.board.elements.Piece

object BoardSerializer : Serializer<Board> {
    override fun serialize(data: Board): String =
        when (data) {
            is BoardRun -> "run ${data.turn}"
            is BoardWin -> "win ${data.winner}"
        } + " | " +
                //data.moves.values.joinToString(" ") { "${it.square.index}:${it.color}:${it.isQueen}" }
                data.moves.entries.joinToString(" "){"${it.key}:${it.value.color}:${it.value.isQueen}"}



    /*
    * objetivo é reconstruir um objeto do tipo Board a partir de uma String
    *
    * o conteudo está divido por "|" em que a parte esquerda (left) contem o tipo de jogo
    * (run, win) e informações adicionais na direita (right) contem os movimentos (moves)
    *
    * constroi um novo moves(map) em que cada key(posição(int)) e cada valor(piece) representa
    * o piece joagada do jogador
    *
    * transforma cada string "int:piecie" numa lista com dois elementos
    * converte cada par num Par Square(...) para construir um mapa moves
    *
    * parte da esquerda(left), divide contendo o tipo de jogo (run, win) e o jogador ou vencedor
    * */
    override fun deserialize(text: String): Board {
        text.split(" | ").let { (left, right) ->
            val moves =
                if (right.isBlank()) mapOf() // cria um mutable map vazio se o texto estiver em branco
                else right.split(" ")
                    .map { it.split(":") }
                    .associate { (squareID, squareStr, queen) ->
                        val square = squareID.toSquare()
                        val queenB: Boolean = queen == "true"
                        square to Piece(squareStr.toPlayer(), queenB)


                        //(index.toInt()) to Piece(squareStr.toSquareOrNull()!!,squareStr.toPlayer()) // associa Int e Square ao objeto Piece
                    } // associando o índice (Int) e a representação de Square ao objeto Piece

            val (type, player) = left.split(" ")
            return when (type) {
                "run" -> BoardRun(moves.toMap(), player.toPlayer())
                "win" -> BoardWin(moves.toMap(), player.toPlayer())
                else -> error("Invalid board type $type")
            }
        }
    }
}