package isel.tds.damas.model.board.elements

/*
    A utilização de value classes com @JvmInline melhora a performance ao evitar a criação de objetos desnecessários,
    proporcionando abstração e segurança de tipo sem overhead de memória.
*/
@JvmInline
value class Row(val index: Int) {

    /*
        Verifica se o índice da linha está dentro do intervalo válido.
        Caso contrário, uma exceção é lançada indicando um índice inválido.
    */
    init {
        require(index in 0 until BOARD_DIM) {
            "Invalid row index: $index"
        }
    }

    /*
        Retorna o digito associado à linha, começando no indice '8' a '1'.
    */
    val digit: Char
        get() = (BOARD_DIM - index).digitToChar()


    /*
        O companion object permite criar membros estáticos, como values, que pertencem à classe Row.
        Neste caso é criada uma lista imutável de objetos Row, um para cada índice, até BOARD_DIM.
     */
    companion object {
        val values: List<Row> = List(BOARD_DIM) { Row(it) }

    }
}


/*
    A função permite fazer a conversão, com base no digito da linha para um objeto Row.
*/
fun Char.toRowOrNull(): Row? {
    val index = BOARD_DIM.digitToChar() - this
    return if (index in 0 until BOARD_DIM) Row(index) else null
}