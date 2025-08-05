package com.ekyrizky.complayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ekyrizky.complay.designsystem.components.indicator.ComplayIndicator
import com.ekyrizky.complay.designsystem.utils.BufferingSize
import com.ekyrizky.complay.designsystem.utils.BufferingType
import com.ekyrizky.complayer.ui.theme.ComplayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComplayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ComplayIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        type = BufferingType.DotChase,
                        size = BufferingSize.SMALL
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComplayerTheme {
        Greeting("Android")
    }
}