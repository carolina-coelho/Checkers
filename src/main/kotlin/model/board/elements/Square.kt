package isel.tds.damas.model.board.elements

class Square private constructor(val row: Row, val column: Column) {
    /*
        A propriedade index calcula o índice de uma célula no tabuleiro.
        A fórmula usada combina a linha (row.index) e a coluna (column.index) para determinar a posição no array
        unidimensional que representa o tabuleiro, tendo em conta as dimensões (BOARD_DIM).
    */
    val index: Int
        get() = row.index * BOARD_DIM + column.index

    /*
        A propriedade black devolve true ou false consoante a célula seja preta ou branca.
        A lógica verifica se a soma dos índices da linha e da coluna é ímpar,
        neste caso em particular, a celula é preta se a soma dos indices da coluna e linha forem impares.
    */
    val black: Boolean
        get() = (row.index + column.index) % 2 != 0

    /*
        Esta função sobrepõe o metodo toString para formatar a saída de forma conveniente para a aplicação em questão.
    */
    override fun toString(): String {
        return "${row.digit}${column.symbol}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Square) return false
        if (this.row == other.row && this.column == other.column) return true
        return false
    }

    override fun hashCode(): Int {
        return this.row.index * BOARD_DIM + this.column.index
    }

    /*
        O companion object inicializa uma lista de objetos do tipo Square,
        representando todas as posições possíveis num tabuleiro de dimensão BOARD_DIM x BOARD_DIM (64 posições possiveis).
        Para cada índice, calcula-se a linha e a coluna correspondentes, criando uma instância de Square para cada combinação possivel.
    */
    companion object {
        val values: List<Square> = List(BOARD_DIM * BOARD_DIM) {
            val row = Row(it / BOARD_DIM)
            val column = Column(it % BOARD_DIM)
            Square(row, column)
        }
    }
}

fun Square(row: Int, column: Int): Square {
    return Square.values[row * BOARD_DIM + column]
}


/*
    Esta função de extensão , converte uma string , caso exista , num objeto Square.
    Se a string for nula ou inválida (não puder ser convertida), lança uma exceção do tipo, IllegalArgumentException.
    Caso contrário, delega a conversão para o metodo toSquareOrNull(), que lida com a conversão propriamente dita.
*/
fun String?.toSquare(): Square {
    return this?.toSquareOrNull() ?: throw IllegalArgumentException("Invalid input string for Square")
}

/*
    Esta função de extensão converte uma ‘string’ de dois caracteres num objeto Square.
    Caso a ‘string’ não tenha exatamente dois caracteres retorna null.
    O primeiro carácter rowChar é validado entre '8' a '1', representando assim, as linhas.
    O segundo carácter columnChar é validado entre os caracteres 'a' .. 'h', representando assim, as colunas.
*/
fun String.toSquareOrNull(): Square? {
    if (this.length != 2) return null

    val rowChar = this[0]
    val columnChar = this[1]

    if (rowChar !in '1'..'8') return null

    if (columnChar !in 'a'..'h') return null
    /*
        Nesta secção, é calculado o índice da linha a partir de rowChar, subtraindo o seu valor de BOARD_DIM.
        Consequentemente é também calculado o índice da coluna a partir de columnChar, convertendo-o num caracter.
        Por fim, é retornado um objeto Square com os índices de linha e coluna correspondentes.
    */
    val rowIndex = BOARD_DIM - rowChar.digitToInt()

    val columnIndex = columnChar - 'a'


    //return Square(Row(rowIndex), Column(columnIndex))
    return Square(rowIndex, columnIndex)
}





