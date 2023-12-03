package org.wit.macrocount.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentMacroDetailBinding
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroDetailFragment : Fragment() {

    private val args by navArgs<MacroDetailFragmentArgs>()
    private lateinit var detailViewModel: MacroDetailViewModel
    private var _fragBinding: FragmentMacroDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    companion object {
        fun newInstance() = MacroDetailFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_macro_detail, container, false)

        _fragBinding = FragmentMacroDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(MacroDetailViewModel::class.java)
        detailViewModel.observableMacro.observe(viewLifecycleOwner, Observer {
                macro ->
            macro?.let { render(macro) }
            Timber.i("observableMacro ${macro}")
        })
        return root

        //val args = arguments
//        Timber.i("MacroDetailFragment passed args ${args}")
//        Toast.makeText(context,"Macro ID Selected : ${args}", Toast.LENGTH_LONG).show()
//        return view
    }

    private fun render(macro: MacroCountModel) {
        fragBinding.macrovm = detailViewModel
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MacroDetailViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getMacro(args.macroid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

}