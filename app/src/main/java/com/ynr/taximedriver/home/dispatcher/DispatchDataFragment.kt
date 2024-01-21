package com.ynr.taximedriver.home.dispatcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ynr.taximedriver.R
import com.ynr.taximedriver.adapters.DispatchHistoryAdapter
import com.ynr.taximedriver.model.DispatchHistoryModel
import kotlinx.android.synthetic.main.fragment_own_dispatch.*

private const val ARG_PARAM1 = "DispatchHistoryType"
private const val DATA = "DATA"

enum class DispatchHistoryType(value: String){
    OWN_DISPATCH("own_dispatch"),
    OWN_NETWORK("own_netowrk")
}

class DispatchDataFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var type: DispatchHistoryType
    private lateinit var data: ArrayList<DispatchHistoryModel.DispatchHistory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = DispatchHistoryType.valueOf(it.getString(ARG_PARAM1, DispatchHistoryType.OWN_DISPATCH.name))
            data = it.getParcelableArrayList(DATA)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_own_dispatch, container, false)
    }

    private lateinit var dispatchHistoryAdapter: DispatchHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == DispatchHistoryType.OWN_DISPATCH){
            root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greenFive))
        } else {
            root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.appLightBlue))
        }

        dispatchHistoryAdapter = DispatchHistoryAdapter(context, data)
        my_hires_recycler_view.adapter = dispatchHistoryAdapter

    }

    companion object {
        @JvmStatic
        fun newInstance(type: DispatchHistoryType, data: List<DispatchHistoryModel.DispatchHistory>) =
                DispatchDataFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, type.name)
                        putParcelableArrayList(DATA, ArrayList())
                    }
                }
    }
}