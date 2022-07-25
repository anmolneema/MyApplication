package com.attendance.office.volley.designation

import android.content.Context
import android.util.Log

import com.android.volley.Request
import com.android.volley.Response

import com.android.volley.VolleyError

import com.android.volley.toolbox.StringRequest
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.attendance.office.AppConstant
import com.attendance.office.AppUtil
import com.attendance.office.volley.department.Department
import com.attendance.office.volley.department.GetDepartment
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope


object GetDesignation {
    private lateinit var callBack: DesignationResponse
    private var designationList: DesignationX? = null
    private val mRequestQueue: RequestQueue? = null
    //val REGISTER_URL = "https://coderpoint.in/myhrms/Api/getdesignation"
    val REGISTER_URL = AppConstant.BASE_URL+"master/designation"
    fun getDesignation(context: Context) {
        val queue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, REGISTER_URL,
            object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    if(response!=null && !response!!.equals("", true)) {
                        designationList = Gson().fromJson(response, DesignationX::class.java)
                        callBack.getAllDesignation(designationList!!.data)
                    }else{
                        callBack.getDesignationError(AppConstant.SOMETHING_WENT_WRONG)
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.d("TAG", "error" + error.toString())
                    if(error.toString().contains(AppConstant.UNKNOWN, true)) {
                        callBack.getDesignationError(AppConstant.NETWORK_NOT_AVAILABLE)
                    }else{
                        callBack.getDesignationError(AppConstant.SOMETHING_WENT_WRONG)
                    }

                }
            }) {

        }

        queue!!.add(stringRequest)
    }

    fun setCallBack(response: DesignationResponse) {
        this.callBack = response

    }

    interface DesignationResponse {
     //   fun getAllDesignation(designationList: Designation)
        fun getAllDesignation(designationList: List<Data>)
        fun getDesignationError(json: String)
    }
}


