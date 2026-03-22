package isel.tds.damas.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import isel.tds.damas.model.board.elements.Piece
import isel.tds.damas.model.player.Player

@Composable
fun PlayerView(
    size: Dp,
    piece: Piece?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier.size(size)
) {
    if (piece == null) {
        Box(
            modifier
                .clickable(onClick = onClick)
        )
    } else {
        val filename = when {
            piece.color == Player.W && piece.isQueen -> "piece_wk"
            piece.color == Player.B && piece.isQueen -> "piece_bk"
            piece.color == Player.W -> "piece_w"
            piece.color == Player.B -> "piece_b"
            else -> "unknown" // Caso haja algum erro inesperado
        }
        Image(
            painter = painterResource("$filename.png"),
            contentDescription = "Player ${piece.color} $filename",
            modifier = modifier
        )
    }
}

@Composable
@Preview
fun PlayerPreview() {
    Column(Modifier.background(Color.Black)) {
        PlayerView(100.dp, null)
        PlayerView(100.dp, Piece(Player.W, isQueen = false))
        PlayerView(100.dp, Piece(Player.B, isQueen = false))
        PlayerView(100.dp, Piece(Player.W, isQueen = true))
        PlayerView(100.dp, Piece(Player.B, isQueen = true))
    }
}
