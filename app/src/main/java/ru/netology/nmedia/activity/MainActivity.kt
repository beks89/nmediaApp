package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.countFormat
import ru.netology.nmedia.viewModel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<PostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.get().observe(this) { post ->
        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likesCount.text = countFormat(post.likesCount)
            shareCount.text = countFormat(post.sharesCount)
            visibleCount.text = countFormat(post.viewsCount)

            likes.setImageResource(
                if (post.likedByMe) R.drawable.ic_like_red
                else R.drawable.ic_like
                )
            }
        }

        binding.likes.setOnClickListener {
            viewModel.like()
        }

        binding.share.setOnClickListener {
            viewModel.share()
        }

    }
}