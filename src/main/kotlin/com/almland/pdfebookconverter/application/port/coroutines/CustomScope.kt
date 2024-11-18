package com.almland.pdfebookconverter.application.port.coroutines

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class CustomScope : CoroutineScope {

    private var parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = parentJob

    fun onStop() {
        parentJob.cancel()
    }
}
