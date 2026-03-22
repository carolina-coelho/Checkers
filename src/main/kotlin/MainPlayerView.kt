package isel.tds.damas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import isel.tds.damas.model.board.elements.Piece
import isel.tds.damas.model.player.Player
import isel.tds.damas.view.PlayerView

fun main() = application {
    Window(title="Test PlayerView", onCloseRequest = ::exitApplication) {
        PlayerApp()
    }
}
@Composable
fun PlayerApp(){
    var piece by remember { mutableStateOf(Piece(Player.W, false)) }
    var player by remember { mutableStateOf(Player.W) }
    MaterialTheme {
        Column (
            modifier= Modifier.background(Color.Black).fillMaxHeight()
        ){
            PlayerView(100.dp, piece)
            Button(onClick = { player = piece.color.other }) {
                Text("Change Player")
            }
        }
    }
}