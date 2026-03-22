package isel.tds.damas.model.board.elements

const val BOARD_DIM = 8

/*
    A utilização de value classes com @JvmInline melhora a performance ao evitar a criação de objetos desnecessários,
    proporcionando abstração e segurança de tipo sem overhead de memória.
*/
@JvmInline
value class Column(val index: Int) {

    /*
        Verifica se o índice da coluna está dentro do intervalo válido (0 a 7).
        Caso contrário, uma exceção é lançada indicando um índice inválido.
    */
    init {
        require(index in 0 until BOARD_DIM) {
            "Invalid column index: $index"
        }
    }

    /*
        Retorna o símbolo associado à coluna, começando da letra 'a' e incrementando com base no índice da coluna.
    */
    val symbol: Char
        get() = 'a' + index

    /*
        O companion object permite criar membros estáticos, como 'values', que pertencem à classe Column.
        Neste caso é criada uma lista imutável de objetos Column, um para cada índice, até BOARD_DIM.
     */
    companion object {
        val values: List<Column> = List(BOARD_DIM) { Column(it) }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Column) return false
            return Column === other
        }
    }
}

/*
    A função permite fazer a conversão, com base no índice da letra de um caracter para um objeto Column.
*/
fun Char.toColumnOrNull(): Column? {
    val index = this - 'a'
    return if (index in 0 until BOARD_DIM) Column(index) else null
}

