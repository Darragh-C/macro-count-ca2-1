package org.wit.macrocount.ui.user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentUserDetailBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.ui.detail.MacroDetailFragmentDirections
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_user_detail, container, false)

        _fragBinding = FragmentUserDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(requireActivity()).get(UserDetailViewModel::class.java)

        setupMenu()

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userRepo = UserRepo(requireActivity().applicationContext)
        if (userRepo.userId != null) {
            currentUserId = userRepo.userId!!.toLong()
        }

        Timber.i("currentUserId: $currentUserId at user fragment")

        if (currentUserId != null) {
            Timber.i("currentUserId: $currentUserId not null")
            detailViewModel.getUser(currentUserId.toLong())
        }
        val vmUser = detailViewModel.observableUser.value
        Timber.i("vmUser: $vmUser at user fragment")
        detailViewModel.observableUser.observe(viewLifecycleOwner, Observer { render() })
        //Timber.i("observableMacro ${macro}")


        fragBinding.editUser.setOnClickListener() {
            val action = UserDetailFragmentDirections.actionUserDetailFragmentToUserFragment()
            findNavController().navigate(action)
        }

    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_user_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }     }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render() {
        val vmUser = detailViewModel.observableUser.value
        if (vmUser != null) {
            fragBinding.uservm = detailViewModel
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }


//    override fun onResume() {
//        super.onResume()
//        detailViewModel.getUser(currentUserId)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}