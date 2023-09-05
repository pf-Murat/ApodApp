package com.example.apodapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

private const val DEFAULT_INIT_VALUE = 1f
private const val DEFAULT_INIT_VALUE_FOR_ROTATION = 0f
private const val MAX_ZOOM_RATIO = .5f
private const val MIN_ZOOM_RATIO = 3f

@Composable
fun FullScreenImageComponent(
    imageUrl: String,
    hdImageUrl: String,
    onCloseClicked: () -> Unit
) {
    var isHdSelected by remember { mutableStateOf(false) }

    val scale = remember { mutableFloatStateOf(DEFAULT_INIT_VALUE) }
    val rotationState = remember { mutableFloatStateOf(DEFAULT_INIT_VALUE_FOR_ROTATION) }
    val offsetX = remember { mutableFloatStateOf(DEFAULT_INIT_VALUE) }
    val offsetY = remember { mutableFloatStateOf(DEFAULT_INIT_VALUE) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        do {
                            val event = awaitPointerEvent()
                            scale.floatValue *= event.calculateZoom()
                            if (scale.floatValue > 1) {
                                val offset = event.calculatePan()
                                offsetX.floatValue += offset.x
                                offsetY.floatValue += offset.y
                                rotationState.floatValue += event.calculateRotation()
                            } else {
                                scale.floatValue = DEFAULT_INIT_VALUE
                                offsetX.floatValue = DEFAULT_INIT_VALUE
                                offsetY.floatValue = DEFAULT_INIT_VALUE
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) {
            AsyncImage(
                model = if (isHdSelected) hdImageUrl else imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = maxOf(MAX_ZOOM_RATIO, minOf(MIN_ZOOM_RATIO, scale.floatValue))
                        scaleY = maxOf(MAX_ZOOM_RATIO, minOf(MIN_ZOOM_RATIO, scale.floatValue))
                        rotationZ = rotationState.floatValue
                        translationX = offsetX.floatValue
                        translationY = offsetY.floatValue
                    },
            )
        }
        IconButton(
            onClick = onCloseClicked,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = .5f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                tint = Color.Black,
                contentDescription = null
            )
        }

        Switch(
            checked = isHdSelected,
            onCheckedChange = { isHdSelected = !isHdSelected },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
        )
    }


}
