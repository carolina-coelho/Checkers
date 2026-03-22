package isel.tds.damas

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import isel.tds.damas.model.board.elements.Piece
import isel.tds.damas.storage.MongoDriver
import isel.tds.damas.view.*
import isel.tds.damas.viewmodel.AppViewModel

@Composable
@Preview
private fun FrameWindowScope.GridApp(driver: MongoDriver, onExit: () -> Unit) {

    val scope = rememberCoroutineScope()
    val vm: AppViewModel = remember { AppViewModel(driver, scope) }

    var showTargetsChecked by remember { mutableStateOf(true) }
    var autoRefreshChecked by remember { mutableStateOf(true) }


    MaterialTheme {
        MenuBar {
            Menu("Game") {
                Item("Start", onClick = vm::openStartDialog)
                Item("Refresh", enabled = vm.hasClash, onClick = vm::refresh)
                Item("Exit", onClick = onExit)
            }
            Menu("Options") {
                CheckboxItem(
                    text = "Show targets",
                    checked = showTargetsChecked,
                    icon = null,
                    onCheckedChange = { newCheckedState ->
                        // Atualiza o estado
                        showTargetsChecked = newCheckedState
                        vm.updateTargetState()
                        println("Show targets toggled: $newCheckedState")
                    }
                )
                CheckboxItem(
                    text = "Auto-refresh",
                    checked = autoRefreshChecked,
                    icon = null,
                    onCheckedChange = { newCheckedState ->
                        // Atualiza o estado
                        autoRefreshChecked = newCheckedState
                        vm.updateRefreshState()

                        println("Auto-refresh toggled: $newCheckedState")
                    }
                )
            }
        }
        Column() {
            val piece = Piece(vm.sidePlayer, false ) //o isQueen é despresável daí estar a true
            GridView(board= vm.board,vm.board?.moves, onClickCell = vm::play, you = piece, vm.targetState)
            StatusBar(you = piece, board= vm.board,name =vm.name)
        }
        vm.inputName?.let {
            StartOrJoinDialog(
                type = it,
                onCancel = vm::closeStartOrJoinDialog,
                onAction= if (it== InputName.ForStart) vm::start else error("")
            ) }
        vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
       if (vm.isWaiting && autoRefreshChecked) waitingIndicator()
    }
}

fun main() = MongoDriver("bichosFeios").use { driver ->
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = WindowState(size = DpSize.Unspecified),
            title = "Checkers Game"
        ) {
            GridApp(driver, ::exitApplication)
        }
    }
}