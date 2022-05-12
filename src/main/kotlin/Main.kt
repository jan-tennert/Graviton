// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@Composable
@Preview
fun App(windowWidth: Int, windowHeight: Int) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        PlanetSim(windowWidth, windowHeight)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, state = rememberWindowState(size = DpSize(1000.dp, 600.dp))) {
        App(window.width, window.height)
    }
}
