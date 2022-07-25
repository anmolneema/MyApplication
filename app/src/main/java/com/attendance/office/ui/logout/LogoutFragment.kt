package com.attendance.office.ui.logout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.attendance.office.AppConstant
import com.attendance.office.MainActivity
import com.attendance.office.R
import com.attendance.office.SplashScreen
import com.attendance.office.repository.roomdb.employee.Info
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LogoutFragment : Fragment() {

    private lateinit var slideshowViewModel: LogoutViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        slideshowViewModel =
//                ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
//        val textView: TextView = root.findViewById(R.id.text_slideshow)
//        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        ((activity as MainActivity).isHomeFragment) = false
        if (((activity as MainActivity)).db != null) {
            GlobalScope.launch {
                (activity as MainActivity).db!!.infoDao().deleteAllInfo()
                (activity as MainActivity).db!!.employeeDao().deleteAllEmployee()
                (activity as MainActivity).openLogin()
            }
        }

        return root
    }


}