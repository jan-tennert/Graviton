import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import javax.swing.Box

@Composable
fun TimeUI(sim: PlanetSimulation, isSelectedOpen: Boolean, drawOrbits: MutableState<Boolean>) {
    Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
        val text = buildString {
            sim.duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
                val years = days / 365
                val realDays = days % 365
                append("$years years ")
                append("${realDays}d ")
                append("${hours}h ")
                append("${minutes}m ")
                append("${seconds}s ")
            }
        }
        Text(text, color = Color.White)
    }
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        Row {
            Icon(
                Icons.Filled.FastRewind,
                "",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp).clickable {
                    sim.timestep = sim.timestep.previousTimeStep()
                }
            )
            Icon(
                if (sim.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                "",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp).clickable {
                    sim.isPaused = !sim.isPaused
                }
            )
            Icon(
                Icons.Filled.FastForward,
                "",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp).clickable {
                    sim.timestep = sim.timestep.nextTimeStep()
                }
            )
        }
    }
    if(!isSelectedOpen) {
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {
            Row {
                Text("Draw Orbits: ", color = Color.White, fontSize = 15.sp, modifier = Modifier.offset(y = 15.dp))
                Checkbox(drawOrbits.value, onCheckedChange = { drawOrbits.value = it }, colors = CheckboxDefaults.colors(checkedColor = Color.White, uncheckedColor = Color.White, checkmarkColor = Color.Gray))
                Button({
                    sim.extraX = sim.windowWidth / 2f
                    sim.extraY = sim.windowHeight / 2f
                    sim.timestep = Constants.TIMESTEP
                    sim.previousScaleModifier = 1.0
                }, colors = ButtonDefaults.buttonColors(Color.Gray)) {
                    Text("Reset")
                }
            }
        }
    }
}