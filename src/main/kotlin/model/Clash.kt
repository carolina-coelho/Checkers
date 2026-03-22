package isel.tds.damas.model

import isel.tds.damas.model.board.BoardRun
import isel.tds.damas.model.board.BoardWin
import isel.tds.damas.model.board.elements.Square
import isel.tds.damas.model.player.Player
import isel.tds.damas.storage.Storage

/*
* Implementou-se uma value class Id, em que nesta valida se é digito ou caracter
* e se nao está vazio
* */

typealias GameStorage = Storage<Name, Game>

open class Clash(val gs: GameStorage)

//Estado depois de jogar
//outra alteração -> o Game pode ser nullabel uma vez este pode nao ter começado,
//como é no caso do Clash.start verifica se existe game no try
class ClashRun(
    gs: GameStorage,
    val game: Game?,
    val sidePlayer: Player,
    val id: Name,
) : Clash(gs)

fun Clash.deleteIfIsOwner() {
    if (this is ClashRun && sidePlayer == Player.W) gs.delete(id)
}

fun Clash.startClash(name: Name): Clash {
    /* se nao exitir o ficheiro lança uma Exception, indo para o catch, onde nele cria um novo jogo e
    * armazena no creat, logo quando se fizer start outra vez na segunda consola, esta já faz o que é
    * suposto no try porque o ficheiro existe
    * */

    val game = Game()
    gs.create(name, game)
    deleteIfIsOwner()
    return ClashRun(gs, game, Player.W, name).start()
}
/*
fun Clash.joinClash(name: Name): Clash {
    val game = gs.read(name) ?: error("Clash $name not found")
    deleteIfIsOwner()
    return ClashRun(gs, game, Player.b, name)
}
 */

fun Clash.startOrJoinClash(name: Name): Clash {
    val game = gs.read(name)
    val clash:Clash
    clash = if(game==null) startClash(name) else join(name)
    return clash
}

fun Clash.join(name: Name): Clash {
    val game = gs.read(name)
    deleteIfIsOwner()
    return ClashRun(gs, game, Player.B, name)
}

//Só acontece se estiver na ClashRun
private fun Clash.runOper(oper: ClashRun.() -> Game?): Clash {
    check(this is ClashRun) { "Clash not started" }
    return ClashRun(gs, oper(), sidePlayer, id)
}

fun Clash.start() = runOper {
    game?.start().also {
        if (it != null) {
            gs.update(id, it)
        }
    }
}

fun Clash.play(sFrom: Square?, sTo: Square?) = runOper {
    check(game?.playerTurn?.other != this.sidePlayer) { "Not your turn." }
    game?.play(sFrom, sTo).also {
        if (it != null) {
            gs.update(id, it)
        }
    }
}

fun Clash.refresh() = runOper {
    val gameAfter = (gs.read(id) as Game)
    check((game?.board as BoardRun) != gameAfter.board) { "No changes" }
    gameAfter
}

fun Clash.grid() = runOper {
    checkNotNull(gs.read(id)) { "Game $id not found" }
    gs.read(id)
}

class NoChangesException : IllegalStateException("No changes")
class GameDeletedException : IllegalStateException("Game deleted")

fun Clash.canNewBoard() = this is ClashRun && game?.board is BoardWin
