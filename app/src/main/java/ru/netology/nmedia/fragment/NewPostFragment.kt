package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.PostViewModel
import androidx.activity.addCallback
import ru.netology.nmedia.R


class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg?.let(binding.edit::setText)

/*        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.cancelEdit()
            findNavController().navigateUp()
        }
        callback.isEnabled = true*/

        binding.ok.setOnClickListener {
            if (binding.edit.text.isNotBlank()) {
                val content = binding.edit.text.toString()
                viewModel.save(content)
                AndroidUtils.hideKeyboard(requireView())
            }
            findNavController().navigate(R.id.action_newPostFragment_to_feedFragment)
        }
        return binding.root
    }
}