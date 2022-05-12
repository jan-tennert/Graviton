import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun Duration.nextTimeStep() = when(this) {
    1.days -> 2.days
    2.days -> 3.days
    3.days -> 10.days
    10.days -> 30.days
    else -> 1.days
}

fun Duration.previousTimeStep() = when(this) {
    1.hours -> 1.minutes
    10.hours -> 1.hours
    1.days -> 10.hours
    2.days -> 1.days
    3.days -> 2.days
    10.days -> 3.days
    30.days -> 10.days
    else -> 1.days
}

fun String.everyReversed(num: Int, string: String): String {
    val chars = this.reversed()
    return buildString {
        chars.forEachIndexed { index, c ->
            if (index % num == 0) {
                append(string)
            }
            append(c)
        }
    }.reversed()
}
