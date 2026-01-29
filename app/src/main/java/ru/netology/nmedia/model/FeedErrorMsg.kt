package ru.netology.nmedia.model

data class ErrorMsg(
    val postIdError: Long,
    val feedErrorMsg: FeedErrorMsg
)

enum class FeedErrorMsg {
    LIKE_ERROR, UNLIKE_ERROR, REMOVE_ERROR, SAVE_ERROR
}