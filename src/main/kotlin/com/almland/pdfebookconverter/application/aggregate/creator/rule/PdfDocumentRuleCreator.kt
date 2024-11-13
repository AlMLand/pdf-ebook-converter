package com.almland.pdfebookconverter.application.aggregate.creator.rule

import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.application.port.rule.Rule
import com.almland.pdfebookconverter.application.port.rule.RuleCreator
import com.almland.pdfebookconverter.domain.FileTarget
import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream
import java.util.function.Supplier

internal class PdfDocumentRuleCreator(
    private val fB2Creator: Creator,
    private val ePUBCreator: Creator,
    private val content: InputStream
) : RuleCreator<String, InputStream> {

    override fun createRules(argument: String): Map<String, Rule<InputStream>> =
        mapOf(
            FileTarget.FB2.target to createRuleFB2(argument),
            FileTarget.EPUB.target to createRuleEPUB(argument)
        )

    override fun createRule(
        condition: Supplier<Boolean>,
        process: Supplier<InputStream>
    ): Rule<InputStream> = Rule(condition, process)

    private fun createRuleFB2(argument: String): Rule<InputStream> =
        Rule(
            { argument == FileTarget.FB2.target },
            { fB2Creator.create(PdfDocument(content)) }
        )

    private fun createRuleEPUB(argument: String): Rule<InputStream> =
        Rule(
            { argument == FileTarget.EPUB.target },
            { ePUBCreator.create(PdfDocument(content)) }
        )
}
