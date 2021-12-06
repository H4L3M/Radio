package com.mowakib.radio.ui.bs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mowakib.radio.adapter.AppsAdapter
import com.mowakib.radio.databinding.AdAppsFragmentBinding
import com.mowakib.radio.model.PubApp
import com.mowakib.radio.utils.ItemClick

class AdAppsFragment : BottomSheetDialogFragment() {

    private var _binding: AdAppsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdAppsViewModel by activityViewModels()

    private lateinit var recycler: RecyclerView
    private var appsAdapter: AppsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = AdAppsFragmentBinding.inflate(inflater, container, false).apply {
            this.viewModel = viewModel
            this.lifecycleOwner = viewLifecycleOwner

            recycler = recyclerView
        }
        val view: View = binding.root

        return view
    }

    private fun downloadApp(app: PubApp) {
        Toast.makeText(requireContext(), app.name, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.apps.observe(viewLifecycleOwner, {
            it.apply {
                appsAdapter?.apps = it
            }
            Log.d("TAGA", "onCreateView: ${it.size}")
        })

        appsAdapter = AppsAdapter(ItemClick { app -> downloadApp(app) })

        recycler.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = appsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AdAppsFragment()
    }
}