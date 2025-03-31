package com.example.music.data.util

import kotlinx.coroutines.flow.Flow

/**
 * Combines 2 flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param transform The transform function to combine the latest values of the two flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the two flows.
 */
fun <T1, T2, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(flow, flow2) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
        )
    }

/**
 * Combines 3 flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param transform The transform function to combine the latest values of the three flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the three flows.
 */
fun <T1, T2, T3, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    transform: suspend (T1, T2, T3) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(flow, flow2, flow3) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
        )
    }

/**
 * Combines four flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param transform The transform function to combine the latest values of the four flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the four flows.
 */
fun <T1, T2, T3, T4, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    transform: suspend (T1, T2, T3, T4) -> R
): Flow<R> =
    kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
        )
    }

/**
 * Combines six flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param flow5 The fifth flow.
 * @param flow6 The sixth flow.
 * @param transform The transform function to combine the latest values of the six flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the six flows.
 */
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
        )
    }

/**
 * Combines seven flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param flow5 The fifth flow.
 * @param flow6 The sixth flow.
 * @param flow7 The seventh flow.
 * @param transform The transform function to combine the latest values of the seven flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the seven flows.
 */
fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> = kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
        )
    }

/**
 * Combines nine flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow2 The second flow.
 * @param flow3 The third flow.
 * @param flow4 The fourth flow.
 * @param flow5 The fifth flow.
 * @param flow6 The sixth flow.
 * @param flow7 The seventh flow.
 * @param flow8 The eighth flow.
 * @param flow9 The ninth flow.
 * @param transform The transform function to combine the latest values of the nine flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the nine flows.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Flow<R> = kotlinx.coroutines.flow.combine(
        flow,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7,
        flow8,
        flow9
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6,
            args[6] as T7,
            args[7] as T8,
            args[8] as T9,
        )
    }

/**
 * Combines ten flows into a single flow by combining their latest values using the provided transform function.
 *
 * @param flow The first flow.
 * @param flow1 The second flow.
 * @param flow2 The third flow.
 * @param flow3 The fourth flow.
 * @param flow4 The fifth flow.
 * @param flow5 The sixth flow.
 * @param flow6 The seventh flow.
 * @param flow7 The eighth flow.
 * @param flow8 The ninth flow.
 * @param flow9 The tenth flow.
 * @param transform The transform function to combine the latest values of the ten flows.
 * @return A flow that emits the results of the transform function applied to the latest values of the ten flows.
 */
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
    flow: Flow<T>,
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    transform: suspend (T, T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Flow<R> = kotlinx.coroutines.flow.combine(
        flow,
        flow1,
        flow2,
        flow3,
        flow4,
        flow5,
        flow6,
        flow7,
        flow8,
        flow9,
    ) { args: Array<*> ->
        @Suppress("UNCHECKED_CAST")
        transform(
            args[0] as T,
            args[1] as T1,
            args[2] as T2,
            args[3] as T3,
            args[4] as T4,
            args[5] as T5,
            args[6] as T6,
            args[7] as T7,
            args[8] as T8,
            args[9] as T9,
        )
    }