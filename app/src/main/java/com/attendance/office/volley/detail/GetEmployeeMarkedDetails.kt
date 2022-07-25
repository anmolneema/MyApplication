package com.attendance.office.volley.detail

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
import com.attendance.office.volley.markinout.Data
import com.attendance.office.volley.markinout.EmployeeMarkedDetail
import com.attendance.office.volley.markinout.EmployeeMarkedDetailX
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray


object GetEmployeeMarkedDetails {
    private lateinit var callBack: MarkedDetails
    private val mRequestQueue: RequestQueue? = null

    fun getMarkedDetails(empId: String , context: Context) {
        val REGISTER_URL =AppConstant.BASE_URL+  "attendance/" + empId
       // val REGISTER_URL = "https://coderpoint.in/myhrms/Api/attendancereport/" + empId
        val queue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, REGISTER_URL,
            object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    if(response!=null && !response!!.equals("", true)) {
                        var detailList = Gson().fromJson(response, EmployeeMarkedDetailX::class.java)
                          parseJson(detailList!!.data)
                    }else{
                        callBack.getAllMarkedError(AppConstant.SOMETHING_WENT_WRONG)
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.d("TAG", "error" + error.toString())
                    if(error.toString().contains(AppConstant.UNKNOWN, true)) {
                        callBack.getAllMarkedError(AppConstant.NETWORK_NOT_AVAILABLE)
                    } else{
                        callBack.getAllMarkedError(AppConstant.SOMETHING_WENT_WRONG)
                    }

                }
            }) {

        }

        queue!!.add(stringRequest)
    }

    fun parseJson(response: List<Data>){
        callBack.getAllMarkedDetails(response)

    }


    fun setCallBack(response: MarkedDetails) {
        this.callBack = response

    }

    interface MarkedDetails {
      //  fun getAllMarkedDetails(json : EmployeeMarkedDetail)
      fun getAllMarkedDetails(json : List<Data>)
        fun getAllMarkedError(json: String)
    }
}


