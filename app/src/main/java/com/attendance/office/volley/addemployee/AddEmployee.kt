package com.attendance.office.volley.addemployee

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
import com.attendance.office.volley.detail.GetEmployeeDetail


object AddEmployee {
    private val mRequestQueue: RequestQueue? = null
  //  val REGISTER_URL = "https://coderpoint.in/myhrms/Api/addemployee"
  val REGISTER_URL = AppConstant.BASE_URL + "employee"
    private lateinit var callBack: EmployeeAdded
    fun addEmployee(employeeModal: Employee, context:Context){

        val queue = Volley.newRequestQueue(context)
        Log.d("TAG", "response Employee model"+ employeeModal.toString() + AppUtil.getImageUrl())
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, REGISTER_URL,
            object : Response.Listener<String?> {
                override  fun onResponse(response: String?) {
                    if(response!=null){
                        if(response.contains(AppConstant.RESPONSE_SUCCESS, true)) {
                            callBack.success(AppConstant.ADDED)
                        }else{
                            callBack.failure(response.trim())
                        }
                    }else{
                        callBack.failure(AppConstant.SOMETHING_WENT_WRONG)
                    }
                    Log.d("TAG","response add Employee"+response.toString())
                }
            },
            object : Response.ErrorListener {
               override fun onErrorResponse(error: VolleyError) {
                   Log.d("TAG","error"+error.toString())
                   if(error.toString().contains(AppConstant.UNKNOWN, true)) {
                       callBack.failure(AppConstant.NETWORK_NOT_AVAILABLE)
                   }else{
                       callBack.failure(AppConstant.SOMETHING_WENT_WRONG)
                   }
                }
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params[AppConstant.FIRST_NAME] = employeeModal.getEmpName()!!.split(" ")[0]
                params[AppConstant.LAST_NAME] = employeeModal.getEmpName()!!.split(" ")[1]
                params[AppConstant.EMP_CODE] = employeeModal.getEmpId()!!
                params[AppConstant.EMP_DEPARTMENT] = java.lang.String.valueOf(employeeModal.getEmpDepartment())
                params[AppConstant.EMP_DESIGNATION] = java.lang.String.valueOf(employeeModal.getEmpDesignation())
                params[AppConstant.EMP_MAILID] = employeeModal.getEmailId()!!
                params[AppConstant.GENDER] = employeeModal.getEmpGender()!!
                params[AppConstant.PASSWORD] = employeeModal.getPassword()!!
                params[AppConstant.EMP_PHONE] = java.lang.String.valueOf(employeeModal.getEmpPhone())
                params[AppConstant.AADHAR_NUMBER] =
                    java.lang.String.valueOf(employeeModal.getEmpAdhaar())
             //   params[AppConstant.IMAGE_URL] = AppUtil.getImageUrl()


                return params
            }


        }

        queue!!.add(stringRequest)
    }

    fun setCallBack(response: EmployeeAdded) {
        this.callBack = response

    }

    interface  EmployeeAdded{
        fun success(success:String)
        fun failure(failure : String)
    }
}