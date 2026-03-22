package isel.tds.damas.model.player

enum class Player {
    W, B, ;

    val other get() = if (this == W) B else W

}

fun String.toPlayerOrNull() = Player.entries.firstOrNull { it.name == this }
fun String.toPlayer() = Player.entries.first { it.name == this }


