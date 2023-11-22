package org.wit.macrocount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.ActivityMacrocountListBinding
import org.wit.macrocount.databinding.FragmentMacroListBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo

class MacroListFragment : Fragment(), MacroCountListener {
    // TODO: Rename and change types of parameters
    private lateinit var app: MainApp
    private lateinit var binding: ActivityMacrocountListBinding
    private lateinit var adapter: MacroCountAdapter
    private lateinit var userRepo: UserRepo
    private var usersDailyMacroObjList = mutableListOf<MacroCountModel>()
    private var currentUserId: Long = 0

    private var _fragBinding: FragmentMacroListBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentMacroListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_macro_count)
        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        fragBinding.recyclerView.adapter = MacroCountAdapter(app.macroCounts.findAll(), this)

        return root


    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MacroListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onMacroCountClick(macroCount: MacroCountModel) {
        TODO("Not yet implemented")
    }

    override fun onMacroDeleteClick(macroCount: MacroCountModel) {
        TODO("Not yet implemented")
    }
}