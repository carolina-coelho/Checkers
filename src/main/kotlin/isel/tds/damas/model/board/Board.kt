package isel.tds.damas.model.board

import isel.tds.damas.model.board.elements.*
import isel.tds.damas.model.player.Player

const val BOARD_SIZE = 8
const val HALF_BOARD_SIZE = BOARD_SIZE / 2

const val UP_DIRECTION = 1
const val DOWN_DIRECTION = -1
const val COLOR_DEPENDENCY_UP = -1
const val COLOR_DEPENDENCY_DOWN = 1

val DIRECTIONS = listOf(UP_DIRECTION, DOWN_DIRECTION)

typealias moves = Map<Square, Piece>

sealed class Board(val moves: moves)
class BoardRun(board: moves, val turn: Player) : Board(board)
class BoardWin(board: moves, val winner: Player) : Board(board)

//adiciona uma peça ao Board
fun BoardRun.addPieceToSquare(square: Square, piece: Piece): BoardRun {
    val updatedMoves = this.moves + (square to piece)
    return BoardRun(updatedMoves, this.turn)
}

//desloca uma peça alterando apenas o square da propriedade Piece
fun BoardRun.updatePiece(pFrom: Piece, sFrom: Square, sTo: Square): BoardRun {
    val newPiece = Piece(this.turn, pFrom.isQueen)
    val newBoard = this.removePieceFromSquare(sFrom).addPieceToSquare(sTo, newPiece)

    if (this.becameQueen(sTo)) {
        val newPieceQ = Piece(this.turn, true)
        val newBoardQ = this.removePieceFromSquare(sFrom).addPieceToSquare(sTo, newPieceQ)
        return BoardRun(newBoardQ.moves, this.turn)
    }
    return BoardRun(newBoard.moves, this.turn)
}

//remove uma peça do board
fun BoardRun.removePieceFromSquare(square: Square): BoardRun {
    require(this.squareHasPiece(square))
    val updatedMoves = this.moves - square
    return BoardRun(updatedMoves, this.turn)
}

//verifica se o quadrado tem ou não peça
fun BoardRun.squareHasPiece(posPret: Square): Boolean = this.moves[posPret] != null

fun BoardRun.squareToPiece(square: Square): Piece {
    if (this.moves[square] != null) {
        return requireNotNull(this.moves[square])
    }

    val newPiece = Piece(this.turn, false)
    if (this.becameQueen(square)) {
        return Piece(this.turn, true)
    }
    return newPiece
}

fun Board.playerWithoutPieces(): Boolean {
    val hasWhitePieces = this.moves.values.any { it.color == Player.W }
    val hasBlackPieces = this.moves.values.any { it.color == Player.B }
    // O jogo termina se apenas um jogador tiver peças
    return !(hasWhitePieces && hasBlackPieces)
}

fun Board.play(sFrom: Square, sTo: Square): Board = when (this) {
    is BoardRun -> {
        val pFrom = this.squareToPiece(sFrom)
        check(pFrom.color == this.turn) { "Not your turn." }

        val isQueenMove = pFrom.isQueen

        val newBoard = playMove(sTo, sFrom, isQueenMove)

        if (isWinner(newBoard)) {
            BoardWin(newBoard.moves, this.turn)
        } else {
            newBoard
        }
    }

    is BoardWin -> error("Game over")
}

private fun BoardRun.playMove(sTo: Square, sFrom: Square, queen: Boolean): Board {
    require(this.squareHasPiece(sFrom))

    val mandatoryEat = canEat(this, queen)
    val mandatoryEatQueen = canEat(this, !queen)

    if (!queen && mandatoryEatQueen && !canEatWithThisMove(this, sTo, sFrom, true)) {
        error("There is a mandatory piece to eat.")
    }

    if (mandatoryEat && !canEatWithThisMove(this, sTo, sFrom, queen)) {
        error("There is a mandatory piece to eat.")
    }

    return if (mandatoryEat) {
        val newBoardWithEat = eat(sFrom, sTo, this, queen)
        if (canEat(BoardRun(newBoardWithEat.moves, this.turn), queen)) {
            this.handleMultiCapture(newBoardWithEat, sTo, queen)
        } else {
            BoardRun(newBoardWithEat.moves, this.turn.other)
        }
    } else {
        BoardRun(movePiece(sFrom, sTo, this, queen).moves, this.turn.other)
    }
}

// Função externa para tentar capturar
fun tryCapture(boardWithEat: BoardRun, sTo: Square, color: Player, newPFrom: Piece, queen: Boolean): BoardRun? {
    for (direction in DIRECTIONS) {
        val toJump = boardWithEat.evaluateDiagonals(sTo.column.index, sTo.row.index, color, newPFrom.color, direction, queen)
        if (toJump != null) {
            val newBoardWithEat = eat(sTo, toJump, boardWithEat, queen)
            val newBoardWithMultipleCapture = BoardRun(newBoardWithEat.moves, newBoardWithEat.turn.other)
            return if (canEat(newBoardWithMultipleCapture, queen)) {
                newBoardWithMultipleCapture.handleMultiCapture(newBoardWithEat, sTo, queen)
            } else {
                newBoardWithMultipleCapture
            }
        }
    }
    return null
}

private fun BoardRun.handleMultiCapture(boardWithEat: BoardRun, sTo: Square, queen: Boolean): BoardRun {
    val newPFrom = squareToPiece(sTo)

    // Tenta capturar com a cor original
    val capturedWithOriginalColor = tryCapture(boardWithEat, sTo, newPFrom.color, newPFrom, queen)
    if (capturedWithOriginalColor != null) return capturedWithOriginalColor

    // Se for rainha, tenta capturar
    if (queen) {
        val oppositeColor = if (newPFrom.color == Player.W) Player.B else Player.W
        return tryCapture(boardWithEat, sTo, oppositeColor, newPFrom, queen) ?: BoardRun(boardWithEat.moves, boardWithEat.turn.other)
    }

    // Caso não haja mais capturas possíveis, retorna o tabuleiro após o movimento
    return BoardRun(boardWithEat.moves, boardWithEat.turn.other)
}

//move a peça consoante as condições permitidas nas regras do jogo
fun BoardRun.movePiece(sFrom: Square, sTo: Square, board: BoardRun, queen: Boolean): Board {
    require(this.squareHasPiece(sFrom))
    // Verifica se o movimento é válido, seja para uma peça normal ou rainha
    require(
        (queen && validPlay(sFrom, sTo, true)) || (!queen && validPlay(sFrom, sTo, false))
    ) { "As posições não são válidas" }
    // Se a peça for uma rainha, a peça movida será rainha, caso contrário, mantém o estado atual
    val movedPiece = if (queen) Piece(this.turn, true) else Piece(this.turn, false) // Mantém a peça normal

    // Atualiza o tabuleiro com a nova posição da peça
    val newBoard = board.updatePiece(movedPiece, sFrom, sTo)
    return BoardRun(newBoard.moves, board.turn)
}

// verifica se a posição atual é uma diagonal na direção correta
fun BoardRun.thePositionIsADiagonalInBack(sFrom: Square, posPret: Square): Boolean {
    require(this.squareHasPiece(sFrom))
    val colorDependency: Int = if (this.turn == Player.W) -1 else 1
    return posPret.row.index == sFrom.row.index - colorDependency &&
            (sFrom.column.index + 1 == posPret.column.index ||
                    sFrom.column.index - 1 == posPret.column.index)
}

//verifica se é uma jogada válida -> quando é rainha
fun BoardRun.validPlay(sFrom: Square, posPret: Square, queen: Boolean): Boolean {
    if (!this.squareHasPiece(sFrom)) return false

    return if (queen) {
        (thePositionIsADiagonalInBack(sFrom, posPret) || thePositionIsADiagonalInFront(sFrom, posPret)) &&
                limitTheBoardWhenQueen(sFrom, posPret)
    } else {
        thePositionIsADiagonalInFront(sFrom, posPret)
    }
}

//para as damas, n rainhas-- as rainhas podem andar nas diagonais acima e nas abaixo
fun BoardRun.thePositionIsADiagonalInFront(sFrom: Square, posPret: Square): Boolean {
    require(this.squareHasPiece(sFrom))
    val colorDependency: Int = if (this.turn == Player.W) 1 else -1
    return posPret.row.index == sFrom.row.index - colorDependency && (sFrom.column.index + 1 == posPret.column.index || sFrom.column.index - 1 == posPret.column.index)
}

//avalia as diagonais e retorna a segunda diagonal do quadrado atual que está vazia, caso esta não esteja, retorna null
fun BoardRun.evaluateDiagonals(
    moveColumnIdx: Int, moveRowIdx: Int, coloDependency: Player, actualColor: Player, direction: Int, queen: Boolean
): Square? {
    // Verifica se a posição está próxima dos limites superior ou inferior.
    val isNearUpperEdge = moveColumnIdx in UPPER_ROW..UPPER_ROW + 1 || moveRowIdx in UPPER_ROW..UPPER_ROW + 1
    val isNearLowerEdge = moveColumnIdx in BOTTOM_ROW - 1..BOTTOM_ROW || moveRowIdx in BOTTOM_ROW - 1..BOTTOM_ROW

    // Retorna null se a posição está na borda e o movimento não é permitido na direção especificada.
    if (!queen &&
        ((isNearUpperEdge && direction == DOWN_DIRECTION && colorDependency(coloDependency) == COLOR_DEPENDENCY_UP) ||
                (isNearLowerEdge && direction == UP_DIRECTION && colorDependency(coloDependency) == COLOR_DEPENDENCY_DOWN))
    ) {
        return null

    }
    // Calcula o quadrado diagonal a ser avaliado.
    val diagonalRowIdx = moveRowIdx + colorDependency(coloDependency)
    val diagonalColumnIdx = moveColumnIdx + direction

    // Verifica se o quadrado diagonal está dentro dos limites.
    if (diagonalRowIdx !in 0..7 || diagonalColumnIdx !in 0..7) return null

    val diagonalSquareToEvaluate = Square(diagonalRowIdx, diagonalColumnIdx)

    // Calcula o quadrado que não deve ter nenhuma peça.
    val emptyRowIdx = diagonalRowIdx + colorDependency(coloDependency)
    val emptyColumnIdx = diagonalColumnIdx + direction

    // Verifica se esse mesmo quadrado está dentro dos limites.
    if (emptyRowIdx !in 0..7 || emptyColumnIdx !in 0..7) return null

    val squareNeedsToBeEmpty = Square(emptyRowIdx, emptyColumnIdx)

    // Valida a condição de captura.
    if ((squareNeedsToBeEmpty.column.index == diagonalSquareToEvaluate.column.index + colorDependency(coloDependency)) || squareNeedsToBeEmpty.column.index == diagonalSquareToEvaluate.column.index - colorDependency(
            coloDependency
        )
    ) {
        if (diagonalSquareToEvaluate in this.moves && squareNeedsToBeEmpty !in this.moves && (this.moves[diagonalSquareToEvaluate]?.color != actualColor)) {
            return squareNeedsToBeEmpty
        }
    }
    return null
}



fun BoardRun.getValidPlays(sFrom: Square, queen: Boolean): List<Square> {
    if (!this.squareHasPiece(sFrom)) return emptyList()

    val currentPiece = this.moves[sFrom]!!

    // Direções de movimento
    val directions = if (queen) {
        listOf(
            Pair(-1, 1), Pair(-1, -1), // Diagonais frente
            Pair(1, 1), Pair(1, -1)   // Diagonais trás
        )
    } else {
        val direction = if (currentPiece.color == Player.W) -1 else 1
        listOf(Pair(direction, 1), Pair(direction, -1)) // Apenas frente
    }

    // Identificar capturas obrigatórias
    val mandatoryCaptures = directions.mapNotNull { (rowStep, colStep) ->
        evaluateDiagonals(
            sFrom.column.index,
            sFrom.row.index,
            currentPiece.color,
            currentPiece.color,
            colStep,
            queen
        )
    }

    // Se existirem capturas obrigatórias, retornamos essas capturas
    if (mandatoryCaptures.isNotEmpty()) {
        return mandatoryCaptures
    }

    // Movimentos da rainha
    fun getQueenMoves(sFrom: Square, directions: List<Pair<Int, Int>>): List<Square> =
        directions.flatMap { (rowStep, colStep) ->
            generateSequence(Pair(sFrom.row.index + rowStep, sFrom.column.index + colStep)) { (currentRow, currentCol) ->
                val nextRow = currentRow + rowStep
                val nextCol = currentCol + colStep
                if (nextRow in 0 until BOARD_SIZE && nextCol in 0 until BOARD_SIZE) {
                    Pair(nextRow, nextCol)
                } else {
                    null
                }
            }.mapNotNull { (currentRow, currentCol) ->
                if (currentRow in 0 until BOARD_SIZE && currentCol in 0 until BOARD_SIZE) { // Garantir índices válidos
                    val nextSquare = Square(currentRow, currentCol)
                    if (validPlay(sFrom, nextSquare, true)) nextSquare else null
                } else null
            }.takeWhile { nextSquare ->
                validPlay(sFrom, nextSquare, true)
            }.toList()
        }

    if (queen) {
        return getQueenMoves(sFrom, directions)
    }

    // Movimentos para peças normais
    return directions.mapNotNull { (rowStep, colStep) ->
        val nextRow = sFrom.row.index + rowStep
        val nextCol = sFrom.column.index + colStep
        if (nextRow in 0 until BOARD_SIZE && nextCol in 0 until BOARD_SIZE) { // Garantir índices válidos
            val nextSquare = Square(nextRow, nextCol)
            if (validPlay(sFrom, nextSquare, queen)) nextSquare else null
        } else null
    }
}