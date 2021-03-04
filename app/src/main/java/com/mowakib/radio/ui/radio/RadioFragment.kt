package com.mowakib.radio.ui.radio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mowakib.radio.adapter.FavRadioAdapter
import com.mowakib.radio.adapter.RadioAdapter
import com.mowakib.radio.adapter.RadioClick
import com.mowakib.radio.databinding.FragmentRadioBinding
import com.mowakib.radio.player.IntentUtil
import com.mowakib.radio.player.PlayerActivity
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_LOGO
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_NAME
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_URL

class RadioFragment : Fragment() {

    private lateinit var radioViewModel: RadioViewModel
    private var _binding: FragmentRadioBinding? = null

    private var radioAdapter: RadioAdapter? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        radioViewModel =
            ViewModelProvider(this).get(RadioViewModel::class.java)

        _binding = FragmentRadioBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = radioViewModel

        radioAdapter = RadioAdapter(RadioClick { radio ->
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                action = IntentUtil.ACTION_VIEW
                data = Uri.parse(radio.url)
                putExtra(EXTRA_NAME, radio.name)
                putExtra(EXTRA_LOGO, radio.logo)
                putExtra(EXTRA_URL, radio.url)
            }
            startActivity(intent)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        radioViewModel.radios.observe(viewLifecycleOwner, { radios ->
            radios?.apply {
                radioAdapter?.radios = radios
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = radioAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}