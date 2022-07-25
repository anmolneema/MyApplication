package com.attendance.office.volley.detail

import android.content.Context
import android.util.Log

import com.android.volley.Request
import com.android.volley.Response


import com.android.volley.VolleyError

import com.android.volley.toolbox.StringRequest
import com.android.volley.RequestQueue
import com.attendance.office.AppConstant
import com.android.volley.toolbox.Volley
import com.google.gson.Gson



object GetEmployeeDetail {
    private val mRequestQueue: RequestQueue? = null
   // val REGISTER_URL = "https://coderpoint.in/myhrms/Api/Loginauth"
    val REGISTER_URL = AppConstant.BASE_URL+"auth/login"
    private lateinit var callBack: EmployeeDetail

    fun getEmpDetail(mailId : String , password : String , context:Context){

        val queue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, REGISTER_URL,
            object : Response.Listener<String?> {
                override  fun onResponse(response: String?) {
                    Log.d("TAG","response GET Employee" + response!!.toString())
                    if(response!=null && !response!!.equals("", true) && !response!!.contains(AppConstant.INVALID, true)) {
                       var detail = Gson().fromJson(response, EmployeeDetailX::class.java)
                        parseJson(detail!!.data)
                    }else{
                        callBack.getEmployeeError(response.trim())
                    }

                }
            },
            object : Response.ErrorListener {
               override fun onErrorResponse(error: VolleyError) {
                   Log.d("TAG","error"+error.toString())
                   if(error.toString().contains(AppConstant.UNKNOWN, true)) {
                       callBack.getEmployeeError(AppConstant.NETWORK_NOT_AVAILABLE)
                   }else{
                       callBack.getEmployeeError(AppConstant.SOMETHING_WENT_WRONG)
                   }
                }
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params[AppConstant.EMAIL] = mailId!!
                params[AppConstant.PASSWORD_LOGIN] = password!!

                return params
            }


        }

        queue!!.add(stringRequest)
    }

    fun parseJson(response: List<Data>){
      //  val json = JSONArray(response)
        callBack.getEmployeeDetails(response!!)

    }

    fun setCallBack(response: EmployeeDetail) {
        this.callBack = response

    }

    interface  EmployeeDetail{
      //  fun getEmployeeDetails(json: JSONArray)
        fun getEmployeeDetails(json: List<Data>)
        fun getEmployeeError(json: String)
    }
}