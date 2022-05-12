import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListUI(bodies: List<Body>, selectedBodyState: MutableState<Body?>, windowWidth: Int, windowHeight: Int) {
    val width = windowWidth / 6
    val height = windowHeight / 3
    val offsetY = windowHeight / 3
    Box(modifier = Modifier.size(width.dp, height.dp).offset(0.dp, offsetY.dp).background(Color.LightGray.copy(alpha = 0.2f))) {
        LazyColumn {
            bodies.forEach {
                item {
                    Text(it.name, fontSize = 20.sp, color = Color.White, modifier = Modifier.clickable {
                        selectedBodyState.value = it
                    })
                }
            }
        }
    }
}