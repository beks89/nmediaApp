package ru.netology.nmedia

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post (
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        )

        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likesCount.text = countFormat(post.likesCount)
            shareCount.text = countFormat(post.sharesCount)
            visibleCount.text = countFormat(post.viewsCount)


            likes.setOnClickListener {
                post.likedByMe = !post.likedByMe
                if (post.likedByMe) {
                    post.likesCount++
                }
                else {
                    post.likesCount
                }

                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_like_red
                    else R.drawable.ic_like
                )
            }

            share.setOnClickListener {
                post.sharesCount++
            }
        }

        }
    }