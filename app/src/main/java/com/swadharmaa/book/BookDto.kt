package com.swadharmaa.book

data class BookBody(
    var author: String,
    var categoryId: String,
    var description: String,
    var genre: List<Any>,
    var keywords: List<Any>,
    var name: String,
    var yearOfPublish: String
)

data class HomeDto(
    var contentFound: Boolean,
    var `data`: List<HomeData>,
    var message: String,
    var status: Int
)

data class HomeData(
    var books: List<HomeBookData>,
    var genre: String
)

data class HomeBookData(
    var _id: String,
    var author: String,
    var content: String,
    var description: String,
    var genre: String,
    var name: String,
    var thumbnail: String,
    var yearOfPublish: String
)

data class SingleBook(
    var libraryId: String,
    var userId: Any
)

data class BookDto(
    var contentFound: Boolean,
    var `data`: BookData,
    var message: String,
    var status: Int
)

data class BookListDto(
    var contentFound: Boolean,
    var `data`: List<BookData>,
    var message: String,
    var status: Int
)

data class BookData(
    var _id: Any,
    var author: String,
    var content: String,
    var createdAt: String,
    var description: String,
    var genre: List<String>,
    var name: String,
    var thumbnail: String,
    var updatedAt: String,
    var yearOfPublish: String,
    var isBookmark: Boolean?
)