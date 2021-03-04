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
import com.bumptech.glide.Glide
import com.mowakib.emp.Emp
import com.mowakib.radio.adapter.FavRadioAdapter
import com.mowakib.radio.adapter.RadioAdapter
import com.mowakib.radio.adapter.RadioClick
import com.mowakib.radio.databinding.FragmentRadioBinding
import com.mowakib.radio.player.IntentUtil
import com.mowakib.radio.player.PlayerActivity
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_LOGO
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_NAME
import com.mowakib.radio.player.PlayerActivity.Companion.EXTRA_URL
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.get
import com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mowakib.radio.R
import com.mowakib.radio.databinding.ExoAudioPlayerBinding
import com.mowakib.radio.databinding.PlayerActivityBinding


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

            binding.playerView.visibility = View.VISIBLE

            val radioName = binding.playerView.findViewById<TextView>(R.id.exo_text)
            radioName.text = radio.name

            val radioLogo = binding.playerView.findViewById<ImageView>(R.id.radio_logo)
            radioLogo.setImageResource(R.drawable.notif_bg)

            Emp.with(requireContext()).init(binding.playerView).load(radio.url)
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