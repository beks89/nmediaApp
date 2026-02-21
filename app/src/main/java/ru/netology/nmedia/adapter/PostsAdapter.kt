package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.countFormat
import ru.netology.nmedia.util.loadAvatar
//import ru.netology.nmedia.util.loadAttachmentImage
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.util.loadAttachmentImage
import androidx.core.view.isVisible


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun openWebPage(url: String?) {}
    fun openPost(post: Post) {}
    fun openImage(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            avatar.loadAvatar("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            published.text = post.published
            content.text = post.content
            likes.isChecked = post.likedByMe
            likes.text = "${post.likesCount}"
            //likes.text = countFormat(post.likesCount)
            share.text = countFormat(post.sharesCount)

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            if (post.attachment == null) {
                group.visibility = View.GONE
            } else {
                group.visibility = View.VISIBLE
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> {
                        attachmentFile.loadAttachmentImage("http://10.0.2.2:9999/media/${post.attachment.url}")
                        attachmentFile.setOnClickListener {
                            onInteractionListener.openImage(post)
                        }
                    }

                    AttachmentType.VIDEO -> {
                        attachmentFile.setImageResource(R.drawable.outline_error_24)
                        group.setOnClickListener {
                            onInteractionListener.openWebPage(post.videoUrl)
                        }
                    }
            }
        }

            menu.isVisible = post.ownedByMe

    menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            content.setOnClickListener {
                onInteractionListener.openPost(post)
            }

//            attachmentFile.setOnClickListener{
//                if (post.videoUrl != null) {
//                    onInteractionListener.openWebPage(post.videoUrl)
//                }
//            }

            videoPlay.setOnClickListener{
                if (post.videoUrl != null) {
                    onInteractionListener.openWebPage(post.videoUrl)
                }
            }

            likes.setOnClickListener{
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener{
                onInteractionListener.onShare(post)
            }
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}