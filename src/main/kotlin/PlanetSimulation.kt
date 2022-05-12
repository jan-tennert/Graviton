import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlanetSimulation(var windowWidth: Int, var windowHeight: Int) {

    lateinit var previousTimeNano: Instant
    private lateinit var startTime: Instant
    var duration by mutableStateOf(Duration.ZERO)
    var timestep by mutableStateOf(Constants.TIMESTEP)
    private var previousScrollStateValue = 0.0
    var previousScaleModifier by mutableStateOf(1.0)
    var extraX by mutableStateOf(windowWidth / 2f)
    var extraY by mutableStateOf(windowHeight / 2f)
    val bodies = mutableStateListOf<Body>()
    var hasStarted = false
        private set
    var isPaused by mutableStateOf(false)

    init {
        bodies.addAll(listOf(createSun(), createEarth(), createMercury(), createVenus(), createMars(), createJupiter(), createSaturn(), createUranus(), createNeptune(), createPluto()))
    }

    fun start() {
        previousTimeNano = Clock.System.now()
        startTime = previousTimeNano
        hasStarted = true
    }

    fun update(scrollState: MutableState<Double>, drawOrbits: Boolean, lockedBody: MutableState<Body?>) {
        if(scrollState.value > previousScrollStateValue) {
            previousScaleModifier *= 1.05
            previousScrollStateValue = scrollState.value.toDouble()
        } else if(scrollState.value < previousScrollStateValue) {
            previousScaleModifier /= 1.05
            previousScrollStateValue = scrollState.value.toDouble()
        }
        if(!isPaused) duration += timestep
        bodies.toList().forEach {
            it.update(bodies, extraX, extraY, timestep, previousScaleModifier, isPaused, drawOrbits, lockedBody, windowWidth, windowHeight)
        }
        /*bodies.toList().forEach { body ->
            bodies.toList().forEach {
                val x1 = body.position.x
                val y1 = body.position.y
                val x2 = it.position.x
                val y2 = it.position.y
                val width1 = body.initialSize
                val width2 = it.initialSize
                //check if bodies are overlapping
                /*if(body != it && x1 + width1.value > x2 && x1 < x2 + width2.value && y1 + width1.value > y2 && y1 < y2 + width2.value) {
                    val item = if(body.mass < it.mass) body else it
                    bodies.remove(item)
                }*/
            }
        }*/
    }

    @Composable
    fun drawAll(
        selectedBodyState: MutableState<Body?>,
        drawOrbits: MutableState<Boolean>,
        lockedBody: MutableState<Body?>
    ) {
        bodies.forEach {
            it.draw(previousScaleModifier, selectedBodyState, drawOrbits, extraX, extraY)
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlanetSim(windowWidth: Int, windowHeight: Int) {
    val selectedBody = remember { mutableStateOf<Body?>(null) }
    val scrollState = remember { mutableStateOf(0.0) }
    val sim = remember { PlanetSimulation(windowWidth, windowHeight) }
    var backgroundWidth by remember { mutableStateOf(windowWidth) }
    var backgroundHeight by remember { mutableStateOf(windowHeight) }
    val drawOrbits = remember { mutableStateOf(false) }
    val lockedBody = remember { mutableStateOf<Body?>(null) }
    Image(painterResource("background.jpg"), "", modifier = Modifier.fillMaxSize())
    if(!sim.hasStarted) {
        sim.start()
    }
    if(sim.hasStarted) {
        Box(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .onSizeChanged {
                sim.windowWidth = it.width
                sim.windowHeight = it.height
                backgroundWidth = it.width
                backgroundHeight = it.height
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    sim.extraX += dragAmount.x
                    sim.extraY += dragAmount.y
                }
            }
            .onPointerEvent(PointerEventType.Scroll) {
                val delta = it.changes[0].scrollDelta.y.toDouble()
                scrollState.value -= delta
            }
        )
        sim.drawAll(selectedBody, drawOrbits, lockedBody)
        TimeUI(sim, selectedBody.value != null, drawOrbits)
        ListUI(sim.bodies, selectedBody, backgroundWidth, backgroundHeight)
        selectedBody.value?.let {
            BodyUI(it, sim, backgroundWidth, backgroundHeight, selectedBody, lockedBody)
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            val now = Clock.System.now()
            if(sim.previousTimeNano.periodUntil(now, TimeZone.UTC).nanoseconds < 16.milliseconds.inWholeNanoseconds) { delay(1); continue }
            sim.previousTimeNano = now
            if (sim.hasStarted)
                sim.update(scrollState, drawOrbits.value, lockedBody)
        }
    }
}