package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getAll(): List<Post>
//    fun likeById(id: Long)
//    fun unlikeById(id: Long)
    fun shareById(id: Long)
//    fun removeById(id: Long)
//    fun save(post: Post)
    fun likeByIdAsync(id: Long, callback: PostCallback<Post>)
    fun unlikeByIdAsync(id: Long, callback: PostCallback<Post>)
    fun saveAsync(post: Post, callback: PostCallback<Post>)
    fun removeByIdAsync(id: Long, callback: PostCallback<Unit>)
    fun getAllAsync(callback: PostCallback<List<Post>>)

    interface PostCallback<T> {
        fun onSuccess(result: T)
        fun onError(e: Exception)
    }
}