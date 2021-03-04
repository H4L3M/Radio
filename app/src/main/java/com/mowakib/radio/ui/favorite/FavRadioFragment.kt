package com.mowakib.radio.ui.favorite

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
import com.mowakib.radio.adapter.RadioClick
import com.mowakib.radio.databinding.FragmentFavRadioBinding
import com.mowakib.radio.player.IntentUtil
import com.mowakib.radio.player.PlayerActivity
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_LOGO
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_NAME
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_URL

class FavRadioFragment : Fragment() {

    private lateinit var favRadioViewModel: FavRadioViewModel
    private var binding: FragmentFavRadioBinding? = null

    private var favRadioAdapter: FavRadioAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favRadioViewModel =
            ViewModelProvider(this).get(FavRadioViewModel::class.java)

        binding = FragmentFavRadioBinding.bind(view)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = favRadioViewModel

        favRadioAdapter = FavRadioAdapter(RadioClick { radio ->
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                action = IntentUtil.ACTION_VIEW
                data = Uri.parse(radio.url)
                putExtra(EXTRA_NAME, radio.name)
                putExtra(EXTRA_LOGO, radio.logo)
                putExtra(EXTRA_URL, radio.url)
            }
            startActivity(intent)
        })

        favRadioViewModel.favRadios.observe(viewLifecycleOwner, {
            it?.apply {
                favRadioAdapter?.radios = it
            }
        })

        binding!!.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favRadioAdapter
        }
    }
}