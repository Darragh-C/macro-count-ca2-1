package org.wit.macrocount.ui.user

import android.app.AlertDialog
import android.net.Uri
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
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import org.wit.macrocount.R
import org.wit.macrocount.customTransformation
import org.wit.macrocount.databinding.FragmentUserDetailBinding
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.ui.detail.MacroDetailFragmentDirections
import org.wit.macrocount.utils.createLoader
import org.wit.macrocount.utils.hideLoader
import org.wit.macrocount.utils.showLoader
import timber.log.Timber

class UserDetailFragment : Fragment() {

    //private val args by navArgs<UserDetailFragmentArgs>()
    private lateinit var detailViewModel: UserDetailViewModel
    private var _fragBinding: FragmentUserDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog

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

        loader = createLoader(requireActivity())
        showLoader(loader,"Loading profile")

        detailViewModel.observableUser.observe(viewLifecycleOwner, Observer {
            render()
            hideLoader(loader)
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()

        Timber.i(" onViewCreated user profile ${detailViewModel.observableUser.value}")
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
        if (detailViewModel.observableUser.value != null) {
            fragBinding.uservm = detailViewModel

            val photoUri = FirebaseAuth.getInstance().currentUser?.photoUrl
            if (photoUri != Uri.EMPTY) {
                Timber.i("Loading Existing profile photo: ${photoUri}")
                Picasso.get()
                    .load(photoUri)
                    .resize(400, 400)
                    .transform(customTransformation())
                    .into(fragBinding.profileImage)
            }

        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.loadProfile(detailViewModel.currentUser.uid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}