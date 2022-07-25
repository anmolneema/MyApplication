package com.attendance.office.volley.department

import android.content.Context
import android.util.Log

import com.android.volley.Request
import com.android.volley.Response

import com.android.volley.VolleyError

import com.android.volley.toolbox.StringRequest
import com.attendance.office.repository.roomdb.employee.Employee
import com.android.volley.RequestQueue
import com.attendance.office.AppConstant
import com.android.volley.toolbox.Volley
import com.attendance.office.AppUtil
import com.attendance.office.volley.designation.GetDesignation
import com.attendance.office.volley.detail.GetEmployeeDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import java.lang.reflect.Type
import java.util.ArrayList


object GetDepartment {
    private var departmentList: DepartmentX?=null
    private lateinit var callBack: DepartmentResponse
    private val mRequestQueue: RequestQueue? = null
  //  val REGISTER_URL = AppConstant.BASE_URL+"https://coderpoint.in/myhrms/Api/getdepartment"
    val REGISTER_URL = AppConstant.BASE_URL+"master/department"
    fun getDepartment(context:Context){
        val queue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, REGISTER_URL,
            object : Response.Listener<String?> {
                override  fun onResponse(response: String?) {
                    if(response!=null && !response!!.equals("", true)) {
                        departmentList = Gson().fromJson(response, DepartmentX::class.java)
                        callBack.getAllDepartment(departmentList!!.data)
                    }else{
                        callBack.getDepartmentError(AppConstant.SOMETHING_WENT_WRONG)
                    }
                }
            },
            object : Response.ErrorListener {
               override fun onErrorResponse(error: VolleyError) {
                   Log.d("TAG","error"+error.toString())
                   callBack.getDepartmentError(AppConstant.NETWORK_NOT_AVAILABLE)
                   if(error.toString().contains(AppConstant.UNKNOWN, true)) {
                       callBack.getDepartmentError(AppConstant.NETWORK_NOT_AVAILABLE)
                   }else{
                       callBack.getDepartmentError(AppConstant.SOMETHING_WENT_WRONG)
                   }
                }
            }) {


        }

        queue!!.add(stringRequest)
    }

    fun setCallBack(response: GetDepartment.DepartmentResponse) {
        this.callBack = response

    }

    interface  DepartmentResponse{
      //  fun getAllDepartment(departmentList: Department)
        fun getAllDepartment(departmentList: List<Data>)
        fun getDepartmentError(json: String)
    }
}


