package com.almland.pdfebookconverter.application.port.rule

import java.util.function.Supplier

internal interface RuleCreator<A, R> {
    
    fun createRules(argument: A): Map<A, Rule<R>>

    fun createRule(
        condition: Supplier<Boolean>,
        process: Supplier<R>
    ): Rule<R>
}
