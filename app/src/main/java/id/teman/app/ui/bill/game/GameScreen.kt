package id.teman.app.ui.bill.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.common.CustomLoading
import id.teman.app.common.GameMenuItem
import id.teman.app.common.TopBar
import id.teman.app.domain.model.bill.ProviderSpec
import id.teman.app.ui.destinations.ProviderScreenDestination
import id.teman.app.ui.theme.Theme

@Composable
@Destination
fun GameScreen(
    navigator: DestinationsNavigator,
    viewModel: GameViewModel = hiltViewModel(),
    providerSpec: ProviderSpec
) {
    val uiState = viewModel.gameUiState
    Scaffold(
        topBar = {
            TopBar(title = providerSpec.titleBar) {
                navigator.popBackStack()
            }
        }, content = {
            val listState = rememberLazyGridState()
            Column {
                LazyVerticalGrid(
                    modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
                    columns = GridCells.Fixed(2),
                    state = listState
                ) {
                    items(uiState.listCategoryGame.size) { index ->
                        with(uiState.listCategoryGame[index]) {
                            GameMenuItem(image = icon, title = name) {
                                navigator.navigate(
                                    ProviderScreenDestination(
                                        ProviderSpec(
                                            icon = icon,
                                            titleBar = name,
                                            titleInput = titleInput,
                                            placeHolder = placeHolder,
                                            caption = caption,
                                            key = key,
                                            information = information,
                                            categoryKey = categoryKey
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
                if (uiState.loading) {
                    Dialog(
                        onDismissRequest = { },
                        DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false
                        )
                    ) {
                        CustomLoading(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        })
}