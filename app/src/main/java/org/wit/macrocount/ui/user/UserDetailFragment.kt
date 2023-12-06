package org.wit.macrocount.ui.user

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
import org.wit.macrocount.databinding.FragmentUserBinding
import org.wit.macrocount.databinding.FragmentUserDetailBinding
import org.wit.macrocount.models.UserModel
import timber.log.Timber

class UserDetailFragment : Fragment() {

    //private val args by navArgs<MacroDetailFragmentArgs>()
    private lateinit var detailViewModel: UserDetailViewModel
    private var _fragBinding: FragmentUserDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    companion object {
        fun newInstance() = UserDetailFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_detail, container, false)

        _fragBinding = FragmentUserDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(UserDetailViewModel::class.java)
        detailViewModel.observableUser.observe(viewLifecycleOwner, Observer {render() })
        //Timber.i("observableMacro ${macro}")

        return root

        //val args = arguments
//        Timber.i("MacroDetailFragment passed args ${args}")
//        Toast.makeText(context,"Macro ID Selected : ${args}", Toast.LENGTH_LONG).show()
//        return view
    }

    private fun render() {
        //fragBinding.
        fragBinding.uservm = detailViewModel
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MacroDetailViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getUser(args.macroid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }