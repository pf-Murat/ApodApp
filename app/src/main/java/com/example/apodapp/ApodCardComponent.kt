package com.example.apodapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.apodapp.ui.theme.ApodItem

@Composable
fun ApodCardComponent(
    apodItem: ApodItem,
    onFavClicked: () -> Unit,
    showLikeButton: Boolean = true,
    showShareButton: Boolean = true,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxSize(.9f)
            .aspectRatio(1f),
    ) {
        Box(modifier = Modifier.fillMaxSize())
        {
            AsyncImage(
                model = apodItem.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(image = Icons.Default.Image),
                error = rememberVectorPainter(image = Icons.Default.BrokenImage),
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = apodItem.date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = MaterialTheme.colorScheme.background.copy(alpha = .4f))
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = apodItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = apodItem.explanation,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showLikeButton) {
                    IconButton(
                        onClick = { onFavClicked.invoke() },
                        modifier = Modifier

                            .size(46.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                                shape = CircleShape
                            )
                    ) {
                        val tint = if (apodItem.isFavorited) Color.Red else Color.White
                        val icon =
                            if (apodItem.isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        Icon(
                            imageVector = icon,
                            tint = tint,
                            contentDescription = null
                        )
                    }
                }
                if (showShareButton) {
                    IconButton(
                        onClick = {
                            context.shareTextWithImage(
                                title = apodItem.title,
                                imageUrl = apodItem.url
                            )
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}