package id.teman.app.ui.ordermapscreen.initiate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.ui.ordermapscreen.common.BottomBlueHeaderInformationWrapper
import id.teman.app.ui.ordermapscreen.common.DriverVaccinationInformation
import id.teman.app.ui.theme.Theme

@Composable
fun BoxScope.InactiveOrderUI(
    isContinueButtonActive: Boolean = false,
    originDetailSpec: PlaceDetailSpec? = null,
    destinationDetailSpec: PlaceDetailSpec? = null,
    orderRequestType: OrderRequestType,
    onContinueButton: (note) -> Unit,
    onOriginClick: (String) -> Unit,
    onDestinationClick: (String) -> Unit
) {
    val state = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .heightIn(screenHeight * 0.5f, screenHeight * 0.8f)
    ) {
        BottomBlueHeaderInformationWrapper {
            DriverVaccinationInformation()
        }
        Card(
            shape = RoundedCornerShape(
                topStart = Theme.dimension.size_32dp,
                topEnd = Theme.dimension.size_32dp
            ),
            modifier = Modifier
                .verticalScroll(state)
                .offset(
                    x = Theme.dimension.size_0dp,
                    y = Theme.dimension.size_56dp
                )
                .padding(bottom = Theme.dimension.size_56dp)
        ) {
            OrderInitiationForm(
                originDetailSpec = originDetailSpec,
                destinationDetailSpec = destinationDetailSpec,
                onDestinationClick = onDestinationClick,
                onOriginClick = onOriginClick,
                onContinueClicked = onContinueButton,
                orderRequestType = orderRequestType,
                isContinueButtonActive = isContinueButtonActive
            )
        }
    }
}