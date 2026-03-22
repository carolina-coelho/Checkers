package isel.tds.damas.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import isel.tds.damas.model.Name
import isel.tds.damas.model.board.*
import isel.tds.damas.model.board.elements.Piece
import isel.tds.damas.model.player.Player


@Composable
fun StatusBar(you: Piece?, board: Board?, name: Name?) {
    //val board = clash?.game?.board
    Row(
        modifier = Modifier
            .width(GRID_WIDTH)
            .background(Color.hsl(32F, 1F, 0.36F)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        // Exibe o jogador "You" apenas quando o estado do tabuleiro for BoardRun
        if (board is BoardRun && you != null) {
            val player = if (you.color == Player.B) "BLACK" else "WHITE"
            Text(
                text = "Game: ${name?.value}",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(Modifier.width(30.dp))
            Text(
                text = "You: $player", // Exibe o jogador associado a 'you'
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(Modifier.width(30.dp))
        }

        // Exibe o texto principal do status
        val (text, playerName) = when (board) {
            null -> "Game not started" to null
            is BoardRun -> "Turn: " to board.turn.name
            is BoardWin -> "Winner: " to board.winner.name
        }
        Text(
            text = "$text${playerName ?: ""}", // Exibe o status do jogo e o nome do jogador
            style= MaterialTheme.typography.h6,
            modifier = Modifier.padding(4.dp) // Reduzido de 32.sp para 28.sp
        )
    }
}

