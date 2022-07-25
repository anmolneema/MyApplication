package com.attendance.office.ui.profile

import android.content.Intent
import android.os.Build
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
import kotlinx.android.synthetic.main.splash_screen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var tvEmpName: TextView? = null
    private var tvAdhaar: TextView? = null
    private var tvEmpId: TextView? = null
    private var tvDeviceMake: TextView? = null
    private var tvDeviceOS: TextView? = null
    private var mModel: String=""
    private var mOsVersion: String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        slideshowViewModel =
//                ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        initView(root)
        getDeviceDetail()
        setDetails()
        return root
    }

    private fun setDetails() {

        //name come from APi
        tvDeviceMake!!.text =  mModel
        tvDeviceOS!!.text =AppConstant.ANDROID + mOsVersion
        tvEmpId!!.text = ((activity as MainActivity).empId)
        tvAdhaar!!.text = ((activity as MainActivity).empAdhaar)
        tvEmpName!!.text =((activity as MainActivity).empName)

    }

    private fun getDeviceDetail() {
        mModel =  android.os.Build.MANUFACTURER + android.os.Build.MODEL
        mOsVersion = android.os.Build.VERSION.RELEASE;
    }

    private fun initView(root: View?) {
        ((activity as MainActivity).isHomeFragment) = false
        tvEmpName = root!!.findViewById(R.id.emp_name)
        tvEmpId = root!!.findViewById(R.id.emp_id)
        tvDeviceMake = root!!.findViewById(R.id.emp_make_model_device)
        tvDeviceOS = root!!.findViewById(R.id.emp_device_os_version)
        tvAdhaar = root!!.findViewById(R.id.emp_adhaar)
    }


}