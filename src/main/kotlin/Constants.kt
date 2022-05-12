import kotlin.time.Duration.Companion.days

object Constants {
    const val AU = 149.6e6 * 1000
    const val G = 6.67428e-11
    const val SCALE = 250 / AU //1 AU == 1 pixels
    val TIMESTEP = 1.days
}