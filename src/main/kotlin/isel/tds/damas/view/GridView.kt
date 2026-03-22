package isel.tds.damas.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import isel.tds.damas.model.board.*
import isel.tds.damas.model.board.elements.*
import isel.tds.damas.model.player.Player

val CELL_SIZE = 50.dp
val HEIGHT_TO_BOARD = CELL_SIZE / 2
val LINE_WIDTH = 5.dp
val GRID_WIDTH = CELL_SIZE * BOARD_SIZE + LINE_WIDTH * (BOARD_SIZE - 1)

@Composable
fun GridView(board: Board?, moves: moves?, onClickCell: (Square, Square) -> Unit, you: Piece?, targetShow:Boolean) {
    val labels = ('a' until 'a' + BOARD_SIZE).toList() // Letras para as colunas
    val selectedSquare = remember { mutableStateOf<Square?>(null) }
    val possibleMoves = remember { mutableStateOf<List<Square>>(emptyList()) }

    Row(
        modifier = Modifier
            .background(Color.hsl(32F, 1F, 0.36F)), // Cor de fundo
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize() // Ajusta o tamanho para incluir labels
                .background(Color.hsl(32F, 1F, 0.36F)) // Cor de fundo do tabuleiro
        ) {
            // Parte Superior (Letras das Colunas)
            Row(
                modifier = Modifier
                    .width(GRID_WIDTH)
                    .height(HEIGHT_TO_BOARD),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(CELL_SIZE / 2)) // Espaço vazio no canto superior esquerdo
                labels.forEach { label ->
                    Text(
                        text = label.toString(),
                        color = Color.Black,
                        modifier = Modifier.width(CELL_SIZE),
                        textAlign = TextAlign.Center
                    )
                }
            }

            val rows = if (board is BoardRun && you != null && you.color == Player.B) {
                // Ordem inversa para jogador "Black"
                (BOARD_SIZE - 1 downTo 0)
            } else {
                // Ordem padrão
                (0 until BOARD_SIZE)
            }

            rows.forEach { row ->
                Row(
                    modifier = Modifier.width(GRID_WIDTH),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = (BOARD_SIZE - row).toString(),
                        color = Color.Black,
                        modifier = Modifier
                            .width(CELL_SIZE / 2) // Reduz a largura do número
                            .height(CELL_SIZE)
                            .background(Color.hsl(32F, 1F, 0.36F)),
                        textAlign = TextAlign.Center
                    )

                    // Determina a ordem das colunas
                    val columns = (0 until BOARD_SIZE)

                    columns.forEach { col ->
                        val currentSquare = Square(row, col)
                        val isBlack = (row + col) % 2 != 0
                        val cellColor = if (isBlack) Color.DarkGray else Color.LightGray

                        // Verifica se a casa contém uma peça
                        val piece = moves?.get(currentSquare)

                        Box(
                            modifier = Modifier
                                .size(CELL_SIZE)
                                .background(cellColor)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedSquare.value == currentSquare) Color.Red else Color.Transparent,
                                    shape = RectangleShape
                                )
                                .clickable {
                                    val previousSquare = selectedSquare.value
                                    if (previousSquare == null) {
                                        // Selecionar a primeira casa
                                        selectedSquare.value = currentSquare

                                        // Atualiza os movimentos possíveis
                                        if (board is BoardRun && board.moves[currentSquare] != null && board.moves[currentSquare]?.color == board.turn) {
                                            val possibleSquares = board.getValidPlays(
                                                currentSquare,
                                                board.moves[currentSquare]!!.isQueen
                                            )
                                            possibleMoves.value = possibleSquares
                                        }
                                    } else {
                                        onClickCell(previousSquare, currentSquare)
                                        selectedSquare.value = null // Reset à seleção
                                        possibleMoves.value = emptyList()
                                    }
                                }
                        ) {

                            // Adiciona a bola verde se o quadrado estiver em possibleMoves
                            if (possibleMoves.value.contains(currentSquare) && targetShow) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.Green,
                                        radius = size.minDimension / 3, // Tamanho proporcional ao quadrado
                                        center = center
                                    )
                                }
                            }

                            // Exibir a peça no tabuleiro
                            piece?.let {
                                PlayerView(100.dp, it)
                            }
                        }
                    }
                }
            }
        }
    }
}
