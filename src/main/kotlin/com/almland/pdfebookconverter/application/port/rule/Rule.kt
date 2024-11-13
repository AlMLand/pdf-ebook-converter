package com.almland.pdfebookconverter.application.port.rule

import java.util.function.Supplier

internal class Rule<R>(
    val condition: Supplier<Boolean>,
    val process: Supplier<R>
)
