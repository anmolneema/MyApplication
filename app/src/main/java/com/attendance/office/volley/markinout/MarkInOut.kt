package com.attendance.office.volley.markinout

import android.R.attr
import com.attendance.office.ui.info.EmployeeModal
import android.R.attr.password
import android.accounts.AccountManager

import android.accounts.AccountManager.KEY_PASSWORD
import android.content.Context
import android.util.Log

import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response

import com.attendance.office.MainActivity

import com.android.volley.VolleyError

import com.android.volley.toolbox.StringRequest
import com.attendance.office.repository.roomdb.employee.Employee
import com.android.volley.RequestQueue
import com.attendance.office.AppConstant
import com.android.volley.toolbox.Volley
import com.attendance.office.AppUtil
import com.attendance.office.repository.roomdb.employee.Info
import com.attendance.office.ui.info.FetchInfoModal
import com.attendance.office.volley.addemployee.AddEmployee
import com.attendance.office.volley.department.GetDepartment
import org.json.JSONObject

import org.json.JSONArray


object MarkInOut {
    private val mRequestQueue: RequestQueue? = null
//    val REGISTER_URL = "https://coderpoint.in/myhrms/Api/attendance"
    private lateinit var callBack: MarkInOutDetail

    fun getMarkIODetail(emp_id: String, info: Info, context: Context) {
        var REGISTER_URL = AppConstant.BASE_URL+"attendance/"+ emp_id+"/add"
        val queue = Volley.newRequestQueue(context)
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, REGISTER_URL,
            object : Response.Listener<String?> {
                override fun onResponse(response: String?) {

                    if (response != null && !response!!.equals("", true) && !response!!.contains(
                            AppConstant.INVALID,
                            true
                        )
                    ) {
                        if (response.trim().contains(AppConstant.SUCCESSFULLY, true)) {
                            Log.d("TAG", "response MarkIN Employee true" + response!!.toString())
                            parseJson(response!!, info)
                        } else {
                            Log.d("TAG", "response MarkIN Employee false" + response!!.toString())
                            callBack.getMarkInOutError(response!!.trim())
                        }

                    } else {
                        callBack.getMarkInOutError(response!!.trim())
                    }

                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.d("TAG", "error" + error.toString())
                    if (error.toString().contains(AppConstant.UNKNOWN, true)) {
                        callBack.getMarkInOutError(AppConstant.NETWORK_NOT_AVAILABLE)
                    } else {
                        callBack.getMarkInOutError(AppConstant.SOMETHING_WENT_WRONG)
                    }
                }
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
//                params[AppConstant.EMP_CODE] = emp_id!!
//                params[AppConstant.ATTDATE] = info.getTime()!!
                if (info.getMark() != null && info.getMark().equals(AppConstant.MARKIN, true)) {
                  //  params[AppConstant.SIGN_IN] = info.getTime()!!.split(" ")[1]
                    //params[AppConstant.SIGN_OUT] = "00:00:00"
                    //params[AppConstant.MARK_IN_PLACE] = info.getAddress()+"**"+info.getLat()+"**"+info.getLon()
                   // params[AppConstant.MARK_OUT_PLACE] =fetchInfoModal!!.getMarkOutPlace()!!
                      params[AppConstant.ACTION] = AppConstant.PUNCH_IN
                    params[AppConstant.LATLONG] = info.getLat() +", " +info.getLon()
                    params[AppConstant.ADDRESS_MARK] = info.getAddress().toString()
                     params[AppConstant.NOTE] =AppConstant.PUNCH_REQUEST

                } else {
                  //  params[AppConstant.MARK_IN_ID] =fetchInfoModal!!.getId()!!
                  //  params[AppConstant.SIGN_IN] = fetchInfoModal!!.getSignInTime()!!
                  //  params[AppConstant.SIGN_OUT] = info.getTime()!!.split(" ")[1]
                  //  params[AppConstant.MARK_IN_PLACE] = fetchInfoModal!!.getMarkInPlace()!!
                //    params[AppConstant.MARK_IN_PLACE] = info.getAddress()+"**"+info.getLat()+"**"+info.getLon()

                    params[AppConstant.ACTION] = AppConstant.PUNCH_OUT
                    params[AppConstant.LATLONG] = info.getLat() +", " +info.getLon()
                    params[AppConstant.NOTE] =AppConstant.PUNCH_REQUEST
                    params[AppConstant.ADDRESS_MARK] = info.getAddress().toString()

                }

                return params
            }


        }

        queue!!.add(stringRequest)
    }

    fun parseJson(response: String?, info: Info) {
        if (info.getMark() != null && info.getMark().equals(AppConstant.MARKIN, true)) {
            callBack.getMarkInOutDetails(response!!, info)
        } else {
            callBack.getMarkInOutDetails(response!!, info)
        }


    }

    fun setCallBack(response: MarkInOutDetail) {
        this.callBack = response

    }

    interface MarkInOutDetail {
        fun getMarkInOutDetails(json: String, info: Info)
        fun getMarkInOutError(json: String)
    }
}