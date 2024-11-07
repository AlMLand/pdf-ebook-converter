package com.almland.pdfebookconverter.application.aggregate.creator.fb2

internal enum class FB2Tag(val tag: String) {
    ROOT("root"), BODY("body"), SECTION("section"), PARAGRAPH("p"), BINARY("binary"), IMAGE("image")
}
