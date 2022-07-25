package com.attendance.office

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Base64
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern
import android.graphics.BitmapFactory
import android.view.View
import android.widget.LinearLayout


object AppUtil {

    var IMAGE_NAME: String = "name"

    //    companion object {
//        private var instance: AppUtil? = null
//
//        fun getInstance(): AppUtil
//        {
//            if (instance == null) {
//                instance = AppUtil()
//            }
//            return instance!!
//        }
//    }
    private var url: String = ""
    private var progress: ProgressDialog? = null
    var preferenceHelper: SharedPreferences? = null

    fun validateAadharNumber(aadharNumber: String?): Boolean {
        val aadharPattern: Pattern = Pattern.compile("\\d{12}")
        var isValidAadhar: Boolean = aadharPattern.matcher(aadharNumber).matches()
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber!!)
        }
        return isValidAadhar
    }

    fun checkLocationPermision(applicationContext: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun checkWritePermission(applicationContext: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun cameraPermission(applicationContext: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    internal object VerhoeffAlgorithm {
        var d = arrayOf(
            intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            intArrayOf(1, 2, 3, 4, 0, 6, 7, 8, 9, 5),
            intArrayOf(2, 3, 4, 0, 1, 7, 8, 9, 5, 6),
            intArrayOf(3, 4, 0, 1, 2, 8, 9, 5, 6, 7),
            intArrayOf(4, 0, 1, 2, 3, 9, 5, 6, 7, 8),
            intArrayOf(5, 9, 8, 7, 6, 0, 4, 3, 2, 1),
            intArrayOf(6, 5, 9, 8, 7, 1, 0, 4, 3, 2),
            intArrayOf(7, 6, 5, 9, 8, 2, 1, 0, 4, 3),
            intArrayOf(8, 7, 6, 5, 9, 3, 2, 1, 0, 4),
            intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
        )
        var p = arrayOf(
            intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            intArrayOf(1, 5, 7, 6, 2, 8, 3, 0, 9, 4),
            intArrayOf(5, 8, 0, 3, 7, 9, 6, 1, 4, 2),
            intArrayOf(8, 9, 1, 6, 0, 4, 3, 5, 2, 7),
            intArrayOf(9, 4, 5, 3, 1, 2, 6, 8, 7, 0),
            intArrayOf(4, 2, 8, 6, 5, 7, 3, 9, 0, 1),
            intArrayOf(2, 7, 9, 3, 8, 0, 6, 4, 1, 5),
            intArrayOf(7, 0, 4, 6, 9, 1, 3, 2, 5, 8)
        )
        var inv = intArrayOf(0, 4, 3, 2, 1, 5, 6, 7, 8, 9)
        fun validateVerhoeff(num: String): Boolean {
            var c = 0
            val myArray = StringToReversedIntArray(num)
            for (i in myArray.indices) {
                c = d[c][p[i % 8][myArray[i]]]
            }
            return c == 0
        }

        private fun StringToReversedIntArray(num: String): IntArray {
            var myArray = IntArray(num.length)
            for (i in 0 until num.length) {
                myArray[i] = num.substring(i, i + 1).toInt()
            }
            myArray = Reverse(myArray)
            return myArray
        }

        private fun Reverse(myArray: IntArray): IntArray {
            val reversed = IntArray(myArray.size)
            for (i in myArray.indices) {
                reversed[i] = myArray[myArray.size - (i + 1)]
            }
            return reversed
        }
    }

    fun setImageUrl(imageToSend: String) {
        url = imageToSend
    }

    fun getImageUrl(): String {
        return url;
    }

    fun showProgress(llLayout: LinearLayout?) {
        try {
           llLayout!!.visibility = View.VISIBLE
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    fun showAuthenticateProgress(llLayout: LinearLayout?) {
        try {
            llLayout!!.visibility = View.VISIBLE
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun cancelProgress(llLayout: LinearLayout?) {
        llLayout!!.visibility = View.GONE

    }

    fun initPreferences(context: Context) {
        preferenceHelper = context!!.getSharedPreferences("MyPreferences", MODE_PRIVATE)
    }

    fun saveImageRecognisor(json: String) {
        val editor: SharedPreferences.Editor = preferenceHelper!!.edit()
//        val gson = Gson()
//        val json = gson.toJson(detector)
        editor.putString(IMAGE_NAME, json)
        editor.apply()
        editor.commit()
    }

    fun getImageRecognisorName():String{

        val json: String = preferenceHelper!!.getString(IMAGE_NAME,"")!!
        return json!!
    }


    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        val imageEncoded: String = Base64.encodeToString(b, Base64.DEFAULT)
        return imageEncoded
    }

    fun saveBitmapInPreferences(bitmap :Bitmap?){
        val editor: SharedPreferences.Editor = preferenceHelper!!.edit()
        if(bitmap!=null) {

            editor.putString(AppConstant.IMAGE_BITMAP, encodeTobase64(bitmap))

        }else{
            editor.putString(AppConstant.IMAGE_BITMAP, "")
        }
        editor.commit()
    }

    fun getBitmapFromPreferences():Bitmap{
       val value =  preferenceHelper!!.getString(AppConstant.IMAGE_BITMAP, "")
        val bitmap = decodeBase64(value)
        return bitmap!!
    }

    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }


}