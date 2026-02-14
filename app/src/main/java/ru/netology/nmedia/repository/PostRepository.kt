package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload

interface PostRepository {
    val data: Flow<List<Post>>
    fun shareById(id: Long)
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun likeByIdAsync(id: Long)
    suspend fun unlikeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun removeByIdAsync(id: Long)
    suspend fun getAllAsync()
    suspend fun updateIsRead()
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media

}