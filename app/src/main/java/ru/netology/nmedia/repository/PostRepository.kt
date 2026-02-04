package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun shareById(id: Long)
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun likeByIdAsync(id: Long)
    suspend fun unlikeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun removeByIdAsync(id: Long)
    suspend fun getAllAsync()

}