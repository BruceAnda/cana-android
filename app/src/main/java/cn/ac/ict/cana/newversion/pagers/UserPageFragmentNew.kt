package cn.ac.ict.cana.newversion.pagers


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.ac.ict.cana.R


/**
 * A simple [Fragment] subclass.
 */
class UserPageFragmentNew : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_user_page_fragment_new, container, false)
    }

}// Required empty public constructor
