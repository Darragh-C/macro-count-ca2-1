package org.wit.macrocount.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.wit.macrocount.R

class MacroDetailFragment : Fragment() {

    companion object {
        fun newInstance() = MacroDetailFragment()
    }

    private lateinit var viewModel: MacroDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_macro_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MacroDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}