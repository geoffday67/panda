package uk.co.sullenart.panda.kitchen_lights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uk.co.sullenart.panda.BaseFragment
import kotlin.math.*

class KitchenLightsFragment : BaseFragment() {
    lateinit var viewModel: KitchenLightsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = KitchenLightsViewModel(this, requireContext()).also {
            lifecycle.addObserver(it)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState()

                MaterialTheme() {
                    Scaffold(
                        scaffoldState = scaffoldState,
                    ) {
                        Column() {
                            LightChooser(viewModel)
                            SideChooser(
                                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
                                left = viewModel.left, onLeft = viewModel::onLeft, onRight = viewModel::onRight
                            )
                        }
                    }

                    if (viewModel.error.isNotEmpty()) {
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(viewModel.error)
                            viewModel.error = ""
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideChooser(
    left: Boolean,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            Modifier.clickable { onLeft() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = left,
                onClick = null,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = "Left"
            )
        }
        Row(
            Modifier.clickable { onRight() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(end = 6.dp),
                text = "Right"
            )
            RadioButton(
                selected = !left,
                onClick = null,
            )
        }
    }
}

@Composable
fun LightChooser(
    viewModel: KitchenLightsViewModel,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        CircularLayout() {
            viewModel.swatches.forEach {
                Swatch(
                    swatch = it,
                    onClick = viewModel::onSwatch,
                )
            }
        }

        if (viewModel.loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun Swatch(
    swatch: Swatch,
    onClick: (Swatch) -> Unit,
) {
    val colour = Color(swatch.red, swatch.green, swatch.blue)
    Box(
        Modifier
            .border(2.dp, Color.Black, CircleShape)
            .size(56.dp)
            .clip(CircleShape)
            .background(colour)
            .clickable { onClick(swatch) }
    )
}

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(Constraints())
        }

        val centreX = constraints.maxWidth / 2
        val centreY = constraints.maxHeight / 2
        var angle = -PI / 2
        val angleIncrement = 2 * PI / placeables.size
        val radius = min(constraints.maxWidth, constraints.maxHeight) * 0.35

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach {
                val x = radius * cos(angle)
                val y = radius * sin(angle)
                it.place(
                    (centreX + x - it.width / 2).roundToInt(),
                    (centreY + y - it.height / 2).roundToInt()
                )
                angle += angleIncrement
            }
        }
    }
}
