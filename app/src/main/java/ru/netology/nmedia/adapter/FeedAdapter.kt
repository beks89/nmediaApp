package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.countFormat
import ru.netology.nmedia.util.loadAvatar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.util.loadAttachmentImage
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeAd = 0
    private val typePost = 1
interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun openWebPage(url: String?) {}
    fun openPost(post: Post) {}
    fun openImage(post: Post) {}
    fun onAdClick(ad: Ad) {}
}

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Post -> typePost
            null -> throw IllegalArgumentException("unknown item type")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // FIXME: students will do in HW
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)
            }
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
            published.text = post.published.toString()
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

    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: Ad) {
            binding.apply {
                image.loadAttachmentImage("${BuildConfig.BASE_URL}/media/${ad.image}")
                image.setOnClickListener {
                    onInteractionListener.onAdClick(ad)
                }
            }
        }
    }

    class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }
}