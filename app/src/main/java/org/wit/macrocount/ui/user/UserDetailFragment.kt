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
import org.wit.macrocount.databinding.FragmentUserDetailBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber

class UserDetailFragment : Fragment() {
    private lateinit var userRepo: UserRepo
    private var currentUserId: Long = 0

    //private val args by navArgs<UserDetailFragmentArgs>()
    private lateinit var detailViewModel: UserDetailViewModel
    private var _fragBinding: FragmentUserDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    companion object {
        fun newInstance() = UserDetailFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("starting user profile detail fragment")
        userRepo = UserRepo(requireActivity().applicationContext)
        val currentUserId = userRepo.userId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_detail, container, false)

        _fragBinding = FragmentUserDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(UserDetailViewModel::class.java)
        detailViewModel.observableUser.observe(viewLifecycleOwner, Observer { render() })
        //Timber.i("observableMacro ${macro}")

        return root

    }

    private fun render() {
        //fragBinding.
        fragBinding.uservm = detailViewModel
    }


    override fun onResume() {
        super.onResume()
        detailViewModel.getUser(currentUserId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}