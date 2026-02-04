package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likesCount: Int = 0,
    val likedByMe: Boolean,
    val sharesCount: Int = 8,
    val viewsCount: Int = 1,
    val videoUrl: String?,
    val isRead: Boolean = false
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likesCount, sharesCount, viewsCount, likedByMe, videoUrl, isRead)

    companion object {
        fun fromDto(dto: Post) = PostEntity(dto.id,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.likesCount,
            dto.likedByMe,
            dto.sharesCount,
            dto.viewsCount,
            dto.videoUrl,
            dto.isRead)
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)