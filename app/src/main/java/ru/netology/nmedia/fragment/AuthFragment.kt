package ru.netology.nmedia.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewModel.AuthViewModel


class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding =
            FragmentAuthBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        binding.signin.setOnClickListener {
            val login = binding.loginInput.text.toString()
            val pass = binding.passwordInput.text.toString()
            viewModel.authenticate(login, pass)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, R.string.Auth_error, Snackbar.LENGTH_LONG).show()
            }
        }
        return binding.root

    }
}