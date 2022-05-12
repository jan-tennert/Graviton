import Constants.AU
import Constants.G
import Constants.SCALE
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration

class Body(
    val name: String,
    private val imageResource: String,
    var mass: Double,
    var yVelocity: Double,
    private val isSun: Boolean,
    private var simX: Double,
    private val initialSize: Dp
) {

    private var distanceToSun: Long = 0L
    private val orbits = mutableListOf<Point>()
    private var xVelocity: Double = 0.0
    var position by mutableStateOf(Point(0f, 0f))
        private set
    private var simY = 0.0

    fun update(
        bodies: List<Body>,
        extraX: Float,
        extraY: Float,
        timestep: Duration,
        scaleModifier: Double,
        isPaused: Boolean,
        drawOrbits: Boolean,
        lockedBody: MutableState<Body?>,
        windowWidth: Int,
        windowHeight: Int
    ) {
        var lExtraX = extraX
        var lExtraY = extraY
        if(name == "Sun") {
            val x = (simX * (SCALE * scaleModifier)) + lExtraX
            val y = (simY * (SCALE * scaleModifier)) + lExtraY
            position = Point(x.toFloat(), y.toFloat())
        }

        if(lockedBody.value != null) {
            //change lExtraX and lExtraY to get moved so that the locked body is always in the center
            val offsetX = (windowWidth / 2) - lockedBody.value!!.position.x //offset to get locked body in center
            val offsetY = (windowHeight / 2) - lockedBody.value!!.position.y // offset to get locked body in center
            println("offsetX: $offsetX")
            println("offsetY: $offsetY")
            //apply offset to this object to move it away
            lExtraX = offsetX
            lExtraY = offsetY
        }

        if(!isPaused) {
            var totalX = 0.0
            var totalY = 0.0
            for (body in bodies) {
                if(body == this) continue
                val (fx, fy) = attractionTo(body)
                totalX += fx
                totalY += fy
            }
            xVelocity += totalX / mass * timestep.inWholeSeconds // F = m / a; a = f / m
            yVelocity += totalY / mass * timestep.inWholeSeconds // F = m / a; a = f / m
            simX += xVelocity * timestep.inWholeSeconds
            simY += yVelocity * timestep.inWholeSeconds
            if(drawOrbits) {
                orbits.add(Point(simX.toFloat(), simY.toFloat()))
            } else if(orbits.isNotEmpty()) {
                orbits.clear()
            }
            val x = (simX * (SCALE * scaleModifier)) + lExtraX
            val y = (simY * (SCALE * scaleModifier)) + lExtraY
            if(lockedBody.value == this) {
                println(x)

            }
            position = Point(x.toFloat(), y.toFloat())
        } else {
            val x = (simX * (SCALE * scaleModifier)) + lExtraX
            val y = (simY * (SCALE * scaleModifier)) + lExtraY
            position = Point(x.toFloat(), y.toFloat())
        }
    }

    private fun attractionTo(other: Body): Pair<Double, Double> {
        val dx = other.simX - simX //distance from the x coordinate to the other x coordinate
        val dy = other.simY - simY //distance from the y coordinate to the other y coordinate
        val d = sqrt(dx.pow(2) + dy.pow(2)) //pythagoras d = sqrt(dx^2 + dy^2)
        if(!isSun && other.isSun) distanceToSun = round(d / 1000).toLong()
        val force = (G * mass * other.mass) / (d.pow(2)) //force = G * mass * other.mass / d^2
        val theta = atan2(dy, dx)
        val fx = force * cos(theta) //cosines of theta
        val fy = force * sin(theta) //sinus of theta
        return fx to fy
    }

    @Composable
    fun draw(sizeModifier: Double, selectedBodyState: MutableState<Body?>, drawOrbits: MutableState<Boolean>, extraX: Float, extraY: Float) {
        Box(modifier = Modifier.offset(position.x.dp, position.y.dp)) {
            Image(painterResource(imageResource), "", modifier = Modifier
                .size((initialSize.value * sizeModifier).dp)
                .clickable {
                    selectedBodyState.value = this@Body
                }
            )
            if(!isSun) {
                Text(
                    "$name\n${distanceToSun.toString().everyReversed(3, ".")}km",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
            }
        }
        if(drawOrbits.value) {
            val updatedOrbits = orbits.map {
                val x = it.x
                val y = it.y
                Point((x * (SCALE * sizeModifier) + extraX).toFloat(), (y * (SCALE * sizeModifier) + extraY).toFloat())
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                updatedOrbits.forEachIndexed { index, point ->
                    val next = updatedOrbits.getOrNull(index + 1)
                    if (next != null) {
                        drawLine(Color.Blue, Offset(point.x, point.y), Offset(next.x, next.y))
                    }
                }
            }
        }
    }

    override fun toString() = "Body(x=${position.x}, y=${position.y}, name=$name)"

}

fun createSun() = Body("Sun", "sun.png", 1.98892 * 10.0.pow(30), 0.0, true, 0.0, 20.dp)
fun createEarth() = Body("Earth", "terre.png", 5.9742 * 10.0.pow(24), 29.783 * 1000, false, -1 * AU, 8.dp)
fun createMars() = Body("Mars", "mars.png", 6.39 * 10.0.pow(23), 24.077 * 1000,false, -1.524 * AU, 4.dp)
fun createMercury() = Body("Mercury", "mercure.png", 0.330 * 10.0.pow(23), -47.4 * 1000,false, 0.387 * AU, 2.dp)
fun createVenus() = Body("Venus", "venus.png", 4.8685 * 10.0.pow(24), -35.02 * 1000,false, 0.723 * AU, 7.5.dp)
fun createJupiter() = Body("Jupiter", "jupiter.png", 1.898 * 10.0.pow(27), 13.07 * 1000, false, 5.203 * AU, 14.dp)
fun createSaturn() = Body("Saturn", "saturne.png", 5.683 * 10.0.pow(26), 9.69 * 1000,false, 9.539 * AU, 14.dp)
fun createUranus() = Body("Uranus", "uranus.png", 8.681 * 10.0.pow(25), 6.81 * 1000, false, 19.8 * AU, 10.dp)
fun createNeptune() = Body("Neptune", "neptune.png", 1.024 * 10.0.pow(26), 5.43 * 1000, false, 30.1 * AU, 10.dp)
fun createPluto() = Body("Pluto", "pluto.png", 1.30900 * 10.0.pow(22), 4.47 * 1000, false, 39.48 * AU, 1.dp)
