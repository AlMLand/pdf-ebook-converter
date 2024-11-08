package com.almland.pdfebookconverter.application.aggregate.creator.fb2

internal enum class FB2Tag(val tag: String) {
    BODY("body"),
    IMAGE("image"),
    PARAGRAPH("p"),
    AUTHOR("author"),
    BINARY("binary"),
    SECTION("section"),
    ROOT("FictionBook"),
    LAST_NAME("last-name"),
    FIRST_NAME("first-name"),
    BOOK_TITLE("book-title"),
    TITLE_INFO("title-info"),
    DESCRIPTION("description")
}
