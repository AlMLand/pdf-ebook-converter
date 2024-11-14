package com.almland.pdfebookconverter.application.aggregate.creator.fb2

internal enum class FB2Tag(val tag: String) {
    BODY("body"),
    IMAGE("image"),
    PARAGRAPH("p"),
    AUTHOR("author"),
    BINARY("binary"),
    SECTION("section"),
    ROOT("FictionBook"),
    NICKNAME("nickname"),
    LAST_NAME("last-name"),
    FIRST_NAME("first-name"),
    BOOK_TITLE("book-title"),
    TITLE_INFO("title-info"),
    DESCRIPTION("description"),
    PROGRAM_USED("program-used"),
    DOCUMENT_INFO("document-info")
}
