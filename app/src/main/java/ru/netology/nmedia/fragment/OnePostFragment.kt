package ru.netology.nmedia.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.fragment.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentOnePostBinding
import ru.netology.nmedia.util.LongArg
import kotlin.getValue


class OnePostFragment : Fragment() {

    companion object {
        var Bundle.idArg: Long by LongArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOnePostBinding.inflate(
            inflater,
            container,
            false
        )

        //val adapter = PostsAdapter(object : OnInteractionListener {
        val interactionListener = object : OnInteractionListener {

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_onePostFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun openWebPage(url: String) {
                val webpage: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (context?.let { intent.resolveActivity(it.packageManager) } != null) {
                    startActivity(intent)
                }
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        }
        val postViewHolder = PostViewHolder(binding.singlePost, interactionListener)

        val postId = arguments?.idArg
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe
            postViewHolder.bind(post)
        }
        return binding.root
    }
}