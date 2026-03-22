package isel.tds.damas.model.board

import isel.tds.damas.model.board.elements.*
import isel.tds.damas.model.player.Player

fun startGame(): BoardRun {/*
        Para cada linha e coluna é criada uma lista de Pares<Int,Piece> no caso se verifique as condições.
        Caso não se verifiquem é atribuido null em vez do Par<Int,Piece>
     */
    val newBoard = (0 until BOARD_SIZE).map { row ->
        (0 until BOARD_SIZE).mapNotNull { col ->
            val square = Square(row, col)
            if (square.black && (row < HALF_BOARD_SIZE - 1 || row > HALF_BOARD_SIZE)) {
                val pieceColor = if (row < HALF_BOARD_SIZE) Player.B else Player.W
                val newPiece = Piece(pieceColor, false)
                square to newPiece
            } else {
                null
            }
        }
    }/*
        Como a newBoard, é composta por uma lista de listas, é necessário obter uma única lista.
        Essa lista é essencialmente a junção das listas de cada dimensão(row e col).
        Para aplicar este conceito aplicamos a função de extenção .flatten().
        Por fim, obtemos uma lista de pares, para ficar no formato pretendido, Map<Int,Piece>,
        utilizamos a função de extenção .toMap()
     */
    val newBoardRet = newBoard.flatten().toMap()


    return BoardRun(newBoardRet, Player.W)
}

//Dependente da cor, a peça vai andar para cima ou para baixo.
//Ou seja, se for preta anda para baixo, caso seja branca, anda para cima
fun colorDependency(player: Player): Int {
    val colorDependency: Int = if (player == Player.W) -1 else 1
    return colorDependency
}

fun canEat(board: BoardRun,queen:Boolean): Boolean {
    // Filtra as peças do jogador atual.
    val currentPlayerPieces:moves = if(queen) board.moves.filter { it.value.color == board.turn && it.value.isQueen }
    else board.moves.filter { it.value.color == board.turn }
    // Lista de direções (para cima e para baixo)
    val directions = listOf(DOWN_DIRECTION, UP_DIRECTION)

    for ((square, piece) in currentPlayerPieces) {
        val columnIdx = square.column.index
        val rowIdx = square.row.index

        // Define a lista de cores: para peças não rainhas, é só o jogador atual
        val colors = if (queen) listOf(board.turn, board.turn.other) else listOf(board.turn)

        // Verifica em todas as direções e cores para rainhas e apenas para a cor do jogador para peças normais
        for (direction in directions) {
            for (color in colors) {
                if (board.evaluateDiagonals(
                        columnIdx,
                        rowIdx,
                        color,
                        piece.color,
                        direction,queen
                    ) != null) return true
            }
        }
    }
    return false
}

fun canEatWithThisMove(board: BoardRun, sTo: Square, sFrom: Square, queen: Boolean): Boolean {
    // Direções e cores a serem avaliadas
    val directions = if (queen) listOf(DOWN_DIRECTION, UP_DIRECTION) else DIRECTIONS
    val colors = if (queen) listOf(board.turn, board.turn.other) else listOf(board.turn)

    // Avaliação das diagonais para cada direção e cor
    for (direction in directions) {
        for (color in colors) {
            val evaluation = board.evaluateDiagonals(
                sFrom.column.index, sFrom.row.index,
                color, board.turn, direction,queen
            )
            if (evaluation == sTo) return true
        }
    }
    return false
}

fun eat(sFrom: Square, sTo: Square, board: BoardRun,queen:Boolean): BoardRun {
    val pFrom = requireNotNull(board.moves[sFrom])
    //se a peça andar para a direita pfrom.row<sto.row
    //se a peça andar para a esquerda pfrom.row> sto.row
    val directionDependency: Int = if (sFrom.column.index < sTo.column.index) 1 else -1
    val directionToPlay: Int = if (sFrom.row.index < sTo.row.index) -1 else 1

    val oppositeSquare = Square(
        sFrom.row.index - directionToPlay,
        sFrom.column.index + directionDependency
    )
    val oppositePiece = board.moves[oppositeSquare]
    requireNotNull(oppositePiece) { "Erro no calculo do quadrado adversário a abater" }

    val newBoardAfterEat = board.removePieceFromSquare(oppositeSquare).removePieceFromSquare(sFrom) // remove a peça que tem de ser comida
    val finalPiece:Piece = if(!queen){
        if (board.becameQueen(sTo) || pFrom.isQueen) {
            Piece(pFrom.color, true) // Torna-se rainha
        } else {
            Piece(pFrom.color, false) // Mantém o estado atual
        }
    }
    else Piece(board.turn, true)
    val newBoard = newBoardAfterEat.addPieceToSquare(sTo, finalPiece)
    return BoardRun(newBoard.moves, board.turn)
}

fun isWinner(board: Board): Boolean = board.playerWithoutPieces()
