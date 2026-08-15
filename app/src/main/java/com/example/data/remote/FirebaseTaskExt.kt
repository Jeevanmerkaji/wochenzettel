package com.example.data.remote

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Bridges a Play Services [Task] (used by both Firebase Auth and Firestore) into a Kotlin
 * suspend call, without pulling in the separate kotlinx-coroutines-play-services artifact
 * just for `.await()`.
 */
suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        val exception = task.exception
        if (task.isSuccessful) {
            cont.resume(task.result)
        } else {
            cont.resumeWithException(exception ?: Exception("Unknown Firebase error"))
        }
    }
}
