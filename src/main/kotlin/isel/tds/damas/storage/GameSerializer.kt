package isel.tds.damas.storage

import isel.tds.damas.model.Game
import isel.tds.damas.model.player.Player


object GameSerializer : Serializer<Game> {

    /*
    * serialize-> premite converter um objeto Game em uma string
    *
    * Game tem como parametros (board:Board?, playerTurn: Player, id:Int?)
    *
    * appendLine-> adiciona uma linha, neste caso tenho de adicionar uma linha para o Board caso esteja presente(se sim
    * chamo o BoardSelizer.serialize), de seguida adiciono outra linha para o plyerTurn
    * */
    override fun serialize(data: Game): String = buildString {
        data.board?.let { appendLine(BoardSerializer.serialize(it)) }
        appendLine(data.playerTurn)
    }

    /*
    * deserialize-> reconstruiu um objeto Game a partir de uma String
    *
    * split-> divide a string em uma lista de três partes, usando \n como separador
    * */
    override fun deserialize(text: String): Game =
        text.split("\n").let { (board, playerTurn) ->
            Game(
                board = if (board.isBlank()) null
                else BoardSerializer.deserialize(board),

                playerTurn = Player.valueOf(playerTurn),
            )
        }
}
