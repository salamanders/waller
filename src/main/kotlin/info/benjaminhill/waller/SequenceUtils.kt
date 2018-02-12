package info.benjaminhill.waller

import kotlin.coroutines.experimental.buildSequence


/** Get a series of values with a fixed step size */
fun buildSequenceDouble(
        startValue: Double = 0.0,
        endValue: Double = 0.0,
        stepSize: Double = 1.0
): Sequence<Double> = buildSequence {
    var current = startValue
    while (current < endValue) {
        yield(current)
        current += stepSize
    }
}

/** Get a series of values with a fixed number of steps */
fun buildSequenceDouble(
        startValue: Double = 0.0,
        endValue: Double = 0.0,
        numberOfSteps: Int = 10
): Sequence<Double> = buildSequenceDouble(
        startValue = startValue,
        endValue = endValue,
        stepSize = (endValue - startValue) / numberOfSteps.toDouble()
)

/** Get a series of values with a fixed time duration, allowing arbitrary many samples in the meantime */
fun buildSequenceTime(
        startValue: Double = 0.0,
        endValue: Double = 1.0,
        msFromNow: Long = 1000
): Sequence<Double> = buildSequence {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + msFromNow
    while (true) {
        val now = System.currentTimeMillis()
        if (now > endTime) {
            break
        }
        val percentDone = (now - startTime).toDouble() / (endTime - startTime)
        yield((endValue - startValue) * percentDone)
    }
}
