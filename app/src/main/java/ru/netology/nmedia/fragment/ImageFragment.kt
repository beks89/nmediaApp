package ru.netology.nmedia.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.fragment.NewPostFragment.Companion.textArg


class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val attachmentUrl = arguments?.textArg
        val url = "http://10.0.2.2:9999/media/$attachmentUrl"

        Glide.with(this)
            .load(url)
            .timeout(10_000)
            .into(binding.singleImage)

        return binding.root
    }
}