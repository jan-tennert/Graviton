import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BodyUI(
    body: Body,
    sim: PlanetSimulation,
    windowWidth: Int,
    windowHeight: Int,
    selectedBodyState: MutableState<Body?>,
    lockedBody: MutableState<Body?>
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = Modifier
                .size(windowWidth.dp / 4, windowHeight.dp)
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {
            Icon(Icons.Filled.Close, "", modifier = Modifier.size(20.dp).align(Alignment.TopEnd).clickable {
                selectedBodyState.value = null
            }, tint = Color.Gray)
            Text(body.name, color = Color.White, fontSize = 20.sp, modifier = Modifier.align(Alignment.TopCenter))
            Button({
                sim.bodies.remove(body)
                selectedBodyState.value = null
            }, colors = ButtonDefaults.buttonColors(Color.Gray), modifier = Modifier.align(Alignment.BottomEnd)) {
                Text("Delete")
            }
            Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                Text(text = "Lock:", fontSize = 15.sp, color = Color.White, modifier = Modifier.offset(y = 15.dp))
                Checkbox(lockedBody.value == body, {
                    lockedBody.value = if (lockedBody.value == body) null else body
                }, colors = CheckboxDefaults.colors(Color.Gray, Color.Gray))
            }
            Column {
                Spacer(modifier = Modifier.height(25.dp))
                Row {
                    Text("Mass", fontSize = 25.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("x2", fontSize = 25.sp, color = Color.White, modifier = Modifier.clickable {
                        body.mass *= 2
                    }.background(Color(4, 4, 117)))
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(":2", fontSize = 25.sp, color = Color.White, modifier = Modifier.clickable {
                        body.mass /= 2
                    }.background(Color(4, 4, 117)))
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Text("Velocity", fontSize = 25.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("x2", fontSize = 25.sp, color = Color.White, modifier = Modifier.clickable {
                        body.yVelocity *= 2
                    }.background(Color(4, 4, 117)))
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(":2", fontSize = 25.sp, color = Color.White, modifier = Modifier.clickable {
                        body.yVelocity /= 2
                    }.background(Color(4, 4, 117)))
                }
            }
        }
    }
}