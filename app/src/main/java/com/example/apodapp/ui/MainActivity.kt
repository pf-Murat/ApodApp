package com.example.apodapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.example.apodapp.MainViewModel
import com.example.apodapp.ui.theme.ApodAppTheme

private val navBarScreens = listOf(
    NavBarScreens.Search,
    NavBarScreens.Explore,
    NavBarScreens.Favorites,
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel = MainViewModel(this.applicationContext)
            ApodAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MainScreenBottomNav(
                            navController = navController,
                            bottomBarItems = navBarScreens
                        )
                    }
                ) { innerPaddings ->
                    Box(modifier = Modifier.padding(innerPaddings)) {
                        MainNavigationHolderScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    val viewModel = MainViewModel(context)
    ApodAppTheme {
        SearchScreen(viewModel, rememberNavController())
    }
}

fun Context.shareTextWithImage(
    title: String,
    imageUrl: String
) {
    val share = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, title)
        putExtra(Intent.EXTRA_STREAM, imageUrl.toUri())
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        type = "image/jpeg"

    }, null)

    startActivity(Intent.createChooser(share, null))
}