package com.almland.pdfebookconverter.application.aggregate.creator.rule

import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.domain.FileTarget
import java.io.InputStream

internal class PdfDocumentRule {
    fun create(
        fB2Creator: Creator,
        ePUBCreator: Creator,
        content: InputStream,
        target: String
    ): InputStream =
        PdfDocumentRuleCreator(fB2Creator, ePUBCreator, content)
            .createRules(target)
            .let { rules ->
                FileTarget.entries
                    .filter { rules[it.target]!!.condition.get() }
                    .map { rules[it.target]!!.process.get() }
                    .firstOrNull()
                    ?: throw NotImplementedError("Target file extension $target, is not implemented")
            }
}
