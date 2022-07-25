package com.attendance.office

import android.Manifest
import android.R.attr
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.attendance.office.repository.roomdb.employee.Employee
import com.attendance.office.repository.roomdb.employee.EmployeeDataBase
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import android.widget.AdapterView

import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.attendance.office.volley.addemployee.AddEmployee
import com.attendance.office.volley.department.Department
import com.attendance.office.volley.department.GetDepartment
import com.attendance.office.volley.designation.Designation
import com.attendance.office.volley.designation.GetDesignation
import com.attendance.office.volley.detail.GetEmployeeDetail
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import androidx.annotation.NonNull
import com.attendance.office.tensorflow.lite.examples.detection.DetectorActivity
import android.R.attr.data
import com.attendance.office.tensorflow.lite.examples.detection.tflite.SimilarityClassifier
import com.attendance.office.volley.designation.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.reflect.Type
import android.icu.lang.UCharacter.GraphemeClusterBreak.V

import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import com.attendance.office.tensorflow.lite.examples.detection.CameraDemoActivity


class SplashScreen : AppCompatActivity(), GetDepartment.DepartmentResponse,
    GetEmployeeDetail.EmployeeDetail, AddEmployee.EmployeeAdded,
    GetDesignation.DesignationResponse {
    private var btnSignUpClick: Boolean = false
    private var empGender: String = "Male"
    private var isfromCamera: Boolean = false
    private var thumbnail: Bitmap? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var historyID: Long? = null
    private var employeeId: String = ""
    private var employeeMailId: String = ""
    private var empl: List<Employee?>? = ArrayList<Employee>()
    private var db: EmployeeDataBase? = null
    var llLogin: LinearLayout? = null
    var llLoginNext: LinearLayout? = null
    var email: TextInputEditText? = null
    var teDomainId: TextInputEditText? = null
    var loginPassword: TextInputEditText? = null
    var phone: TextInputEditText? = null
    var id: TextInputEditText? = null
    var tvFirstName: TextInputEditText? = null
    var tvLastName: TextInputEditText? = null
    var password: TextInputEditText? = null
    var tvDesignation: Spinner? = null
    var tvDepartment: Spinner? = null
    var splash: TextView? = null
    var buttonGradient: GradientDrawable? = null
    var btnSubmit: Button? = null
    var toolbar: Toolbar? = null
    var toolbarLogin: Toolbar? = null
    var empImage: CircleImageView? = null
    var adhaar: TextInputEditText? = null
    var empDesignation: String? = ""
    var empDepartment: String? = ""
    var depMap: ArrayList<Int> = ArrayList<Int>()
    var desMap: ArrayList<Int> = ArrayList<Int>()
    var depMapList: ArrayList<String> = ArrayList<String>()
    var desMapList: ArrayList<String> = ArrayList<String>()
    var progressBar : LinearLayout?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        /*check for permission*/
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        initView();
        valueFromIntent()
        AddEmployee.setCallBack(this)
        GetDesignation.setCallBack(this)
        GetDepartment.setCallBack(this)
        GetEmployeeDetail.setCallBack(this)
        AppUtil.initPreferences(this)
    }

    /**Api for department and designation*/
    private fun getDepartment() {

        if (depMapList.size == 0 && desMapList.size == 0) {
            AppUtil.showProgress(progressBar)
            GlobalScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT) {
                supervisorScope {

                    val department =
                        async { GetDepartment.getDepartment(context = applicationContext) }
                    val designation =
                        async { GetDesignation.getDesignation(context = applicationContext) }

                    try {
                        department.await()
                    } catch (ex: java.lang.Exception) {
                        null
                    }
                    try {
                        designation.await()
                    } catch (ex: java.lang.Exception) {
                        null
                    }

                }

            }
        } else {
            getDepartmentListInAdapter(depMapList!!)
            getDesignationListInAdapter(desMapList!!)
        }
    }

    /***/
    private fun valueFromIntent() {
        var intent = intent
        val isFromLogout = intent.getBooleanExtra(AppConstant.LOGOUT, false)
        if (!isFromLogout) {
            startTimer();
        } else {
            splash!!.visibility = View.GONE
            parkLoginScreen()
        }

    }

    private fun initView() {
        llLoginNext = findViewById(R.id.loginNext)
        llLogin = findViewById(R.id.login)
        email = findViewById(R.id.email)
        teDomainId = findViewById(R.id.teDomainId)
        loginPassword = findViewById(R.id.loginPassword)
        adhaar = findViewById(R.id.adhaar)
        phone = findViewById(R.id.phone)
        empImage = findViewById(R.id.profile_image)
        id = findViewById(R.id.id)
        password = findViewById(R.id.password)
        splash = findViewById(R.id.splash)
        btnSubmit = findViewById(R.id.btnSubmit)
        toolbar = findViewById(R.id.toolbar);
        toolbarLogin = findViewById(R.id.toolbarLogin);
        tvFirstName = findViewById(R.id.name)
        tvLastName = findViewById(R.id.last_name)
        tvDepartment = findViewById(R.id.department)
        tvDesignation = findViewById(R.id.designation)
        toolbar!!.setTitle(applicationContext.resources.getString(R.string.sign_up))
        toolbar!!.setTitleTextColor(applicationContext.resources.getColor(R.color.white))
        toolbarLogin!!.setTitle(applicationContext.resources.getString(R.string.app_name))
        toolbarLogin!!.setTitleTextColor(applicationContext.resources.getColor(R.color.white))
        setSupportActionBar(toolbar);
        initAdapter();
        /*init DB*/
        db = Room.databaseBuilder(applicationContext, EmployeeDataBase::class.java, "employee.db")
            .build()
        empImage!!.setOnClickListener(View.OnClickListener {
            selectImage()
        })
        progressBar =  findViewById(R.id.progressBar);

    }

    private fun initAdapter() {
        val arraySpinner = arrayOf(
            "Male", "Female"
        )
        val s = findViewById<View>(R.id.spinner) as Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arraySpinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s.adapter = adapter

        s.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                // TODO Auto-generated method stub
                empGender = arraySpinner.get(position)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
    }

    private fun startTimer() {
        var timer: CountDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("TAG", "Tick tick")
            }

            override fun onFinish() {
                splash!!.visibility = View.GONE
                parkLoginScreen()
            }
        }.start()
    }

    private fun parkLoginScreen() {
        //check from db user avai;able if not thn login screen otherwise direct home fragment
        isUserAvail()

    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun isUserAvail() {
        GlobalScope.launch {
            empl = db!!.employeeDao().getLastEmp()
            if (empl != null && empl?.size!! > 0) {
//                empl!!.forEach {
//                    employeeId = it!!.getEmpId().toString()
//                    employeeMailId = it!!.getEmailId().toString()
//                }
                openMainActivity()
            } else {
                runOnUiThread {
                    if (!btnSignUpClick) {
                        llLogin!!.visibility = View.GONE
                        llLoginNext!!.visibility = View.VISIBLE
                    } else {
                        llLogin!!.visibility = View.VISIBLE
                        llLoginNext!!.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun btnClick_submit(view: View) {
        //handle authentication of emailId , id integer and password
        val emailId: String = email!!.text.toString()
        val empId: String = id!!.text.toString()
        val passwords: String = password!!.text.toString()
        val phonenumber: String = phone!!.text.toString()
        val adhaarNumber: String = adhaar!!.text.toString()
        val name: String = tvFirstName!!.text.toString()
        val lastname: String = tvLastName!!.text.toString()
        var designation = ""
        if (empDesignation != null)
            designation = empDesignation!!.toString()
        var department = ""
        department = empDepartment!!.toString()
//        if (AppUtil.getImageUrl() == null || thumbnail==null) {
//            hideKeyboard(id!!)
//            Snackbar.make(llLogin!!, AppConstant.IMAGE_UPLOAD, Snackbar.LENGTH_SHORT).show()
//            return
//        }
        if (name.equals("", true)) {
            hideKeyboard(tvFirstName!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_NAME, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (lastname.equals("", true)) {
            hideKeyboard(tvLastName!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_NAME, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (!isValid(emailId)) {
            hideKeyboard(email!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_MAIL, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (empId.equals("", true) || empId.length > 10) {
            hideKeyboard(id!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_ID, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (designation.equals("", true)) {
            hideKeyboard(id!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_DESIGNATION, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (phonenumber.equals("", true) || phonenumber.length != 10) {
            hideKeyboard(phone!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_PHONE, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (department.equals("", true)) {
            hideKeyboard(id!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_DEPARTMENT, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (adhaarNumber.equals("", true) || adhaarNumber[0].equals("0") || adhaarNumber[0].equals(
                "1"
            ) || !AppUtil.validateAadharNumber(adhaarNumber)
        ) {
            hideKeyboard(adhaar!!)
            Snackbar.make(llLogin!!, AppConstant.ADHAAR_ID, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (empGender.equals("", true)) {
            hideKeyboard(phone!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_GENDER, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (passwords.equals("", true) || passwords.length < 8) {
            hideKeyboard(password!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_PASS, Snackbar.LENGTH_SHORT).show()
            return
        }
        AppUtil.showProgress(progressBar)
        GlobalScope.launch {
            val emp: Employee = Employee()
            emp.setEmpName(name + " " + lastname)
            emp.setEmailId(emailId)
            emp.setEmpId(empId)
            emp.setEmpAdhaar(adhaarNumber)
            emp.setEmpPhone(phonenumber)
            emp.setPassword(passwords)
            emp.setTimeStamp(System.currentTimeMillis().toString())
            emp.setEmpDepartment(department)
            emp.setEmpDesignation(designation)
            emp.setEmpGender(empGender)
            historyID = db!!.employeeDao().insert(emp)
            if (historyID != null && historyID != -1L) {
                AddEmployee.addEmployee(employeeModal = emp, context = applicationContext)
            }
            //  isUserAvail()
        }
    }


    fun isValid(email: String?): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pat: Pattern = Pattern.compile(emailRegex)
        return if (email == null) false else pat.matcher(email).matches()
    }

    fun checkLocationPermission(): Boolean {
        return if (AppUtil.checkLocationPermision(applicationContext)
            || AppUtil.checkWritePermission(applicationContext)
        ) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) || shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)//add

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (isfromCamera) {

                            openCamera()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext, "permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                    if (isfromCamera)
                        isfromCamera = false
                    else
                        finish()
                }
                return
            }
        }
    }

    fun hideKeyboard(view: TextInputEditText) {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun selectImage() {
//        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//        builder.setTitle("Add Photo!")
//        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
//            if (options[item] == "Take Photo") {
//
//            } else if (options[item] == "Choose from Gallery") {
//                val intent =
//                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                startActivityForResult(intent, 2)
//            } else if (options[item] == "Cancel") {
//                dialog.dismiss()
//            }
//        })
//        builder.show()
        isfromCamera = true
        if (requestCameraPermission()) {
            openCamera()
        }
    }

    private fun openCamera() {
//        try {
//            isfromCamera = false;
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////        val f = File(Environment.getDataDirectory(), "temp.jpg")
////        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
//            startActivityForResult(intent, 1)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        AppUtil.saveBitmapInPreferences(null)
        val intent = Intent(this, CameraDemoActivity::class.java)
        intent.putExtra("ISFROM", "register")
        startActivityForResult(intent!!, 100)
    }


    fun requestCameraPermission(): Boolean {

        return if (AppUtil.cameraPermission(applicationContext)
        ) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.CAMERA
                )
            ) {
                requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.CAMERA,
                    ),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.CAMERA,
                    ),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
//            if (resultCode == RESULT_OK) {
//                if (requestCode == 1) {
////                var f = File(Environment.getDataDirectory().toString())
////                for (temp in f.listFiles()) {
////                    if (temp.name == "temp.jpg") {
////                        f = temp
////                        break
////                    }
////                }
////                try {
////                    val bitmap: Bitmap
////                    val bitmapOptions = BitmapFactory.Options()
////                    bitmap = BitmapFactory.decodeFile(
////                        f.absolutePath,
////                        bitmapOptions
////                    )
////                    empImage!!.setImageBitmap(bitmap)
////                    val path = (Environment
////                        .getDataDirectory()
////                        .toString() + File.separator
////                            + "Phoenix" + File.separator + "default")
////                    f.delete()
////                    var outFile: OutputStream? = null
////                    val file = File(path, System.currentTimeMillis().toString() + ".jpg")
////                    try {
////                        outFile = FileOutputStream(file)
////                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile)
////                        outFile!!.flush()
////                        outFile!!.close()
////                    } catch (e: FileNotFoundException) {
////                        e.printStackTrace()
////                    } catch (e: IOException) {
////                        e.printStackTrace()
////                    } catch (e: Exception) {
////                        e.printStackTrace()
////                    }
////                } catch (e: Exception) {
////                    e.printStackTrace()
////                }
//                    thumbnail = data!!.getExtras()!!.get("data") as Bitmap?
//                    empImage!!.setImageBitmap(thumbnail)
//                    val image: String = getStringImage(thumbnail!!)
//                    Log.d("image", image)
//                    AppUtil.setImageUrl(image)
//                } else if (requestCode == 2) {
//                    val selectedImage = data!!.data
//                    val filePath = arrayOf(MediaStore.Images.Media.DATA)
//                    val c: Cursor? =
//                        contentResolver.query(selectedImage!!, filePath, null, null, null)
//                    c!!.moveToFirst()
//                    val columnIndex: Int = c.getColumnIndex(filePath[0])
//                    val picturePath: String = c.getString(columnIndex)
//                    c!!.close()
//                    Log.w(
//                        "path of image from gallery......******************.........",
//                        picturePath + ""
//                    )
//                    if (Build.VERSION.SDK_INT >= 29) {
//                        // You can replace '0' by 'cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)'
//                        // Note that now, you read the column '_ID' and not the column 'DATA'
//
//
//                        // now that you have the media URI, you can decode it to a bitmap
//                        try {
//                            this.contentResolver.openFileDescriptor(selectedImage!!, "r")
//                                .use { pfd ->
//                                    if (pfd != null) {
//                                        thumbnail =
//                                            BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)!!
//                                    }
//                                }
//                        } catch (ex: IOException) {
//                        }
//                    } else {
//                        // Repeat the code you already are using
//                        thumbnail = BitmapFactory.decodeFile(picturePath)
//                    }
//
//                    empImage!!.setImageBitmap(thumbnail)
//                    val image: String = getStringImage(thumbnail!!)
//                    Log.d("image", image)
//                    AppUtil.setImageUrl(image)
//                }
//            }

            if (resultCode === 100) {
                //               runOnUiThread {
//                    AppUtil.saveImageRecognisor(DetectorActivity.json);
//                    val json: String = AppUtil.getImageRecognisorName()
//                    Log.d("TAG","json Recognisation" + json.toString())
                //                   val gson = Gson()
                //  val result: SimilarityClassifier.Recognition  = gson.fromJson(json, SimilarityClassifier.Recognition ::class.java)
//                    val listType: Type = object : TypeToken<SimilarityClassifier>() {}.getType()
//                    val result: SimilarityClassifier  = gson.fromJson(json, listType)
                //    Log.d("TAG","json Recognisation" + result.toString())
                //              }
                thumbnail = AppUtil.getBitmapFromPreferences()
                if (thumbnail != null && !thumbnail!!.equals(AppConstant.BLANK)) {
                    empImage!!.setImageBitmap(thumbnail)
                    val image: String = getStringImage(thumbnail!!)
                    AppUtil.setImageUrl(image)
                    Snackbar.make(
                        llLogin!!,
                        AppConstant.SUCCESSFULLY_AUTHENTICATE,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    AppUtil.setImageUrl("")
                    empImage!!.setImageDrawable(resources.getDrawable(R.drawable.user, null))
                    Snackbar.make(llLogin!!, AppConstant.FAILED_AUTHENTICATE, Snackbar.LENGTH_LONG)
                        .show()
                }
//                    Log.d("image", image)

            }else{
                AppUtil.setImageUrl("")
                empImage!!.setImageDrawable(resources.getDrawable(R.drawable.user, null))
                Snackbar.make(llLogin!!, AppConstant.FAILED_AUTHENTICATE, Snackbar.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun getAllDepartment(departmentList: Department) {
//        AppUtil.cancelProgress(progressBar)
//        if (departmentList != null) {
//            departmentList.forEach {
//                depMap!!.put(it.id, it.dep_name)
//                depMapList!!.add(it.dep_name)
//            }
//
//            getDepartmentListInAdapter(depMapList!!)
//        }
//    }

    override fun getAllDepartment(departmentList: List<com.attendance.office.volley.department.Data>) {
        AppUtil.cancelProgress(progressBar)
        if (departmentList != null) {
            departmentList.forEach {
                depMap!!.add(it.id)
                depMapList!!.add(it.deptname)
            }

            getDepartmentListInAdapter(depMapList!!)
        }

    }


//    override fun getAllDesignation(designationList: Designation) {
//        AppUtil.cancelProgress(progressBar)
//        if (designationList != null) {
//            designationList.forEach {
//                desMap!!.put(it.id, it.des_name)
//                desMapList!!.add(it.des_name)
//            }
//
//            getDesignationListInAdapter(desMapList!!)
//        }
//
//    }

    override fun getAllDesignation(designationList: List<Data>) {
        AppUtil.cancelProgress(progressBar)
        if (designationList != null) {
            designationList.forEach {
                desMap!!.add(it.id)
                desMapList!!.add(it.rolename)
            }

            getDesignationListInAdapter(desMapList!!)
        }

    }


    private fun getDesignationListInAdapter(desMapList: java.util.ArrayList<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, desMapList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tvDesignation!!.adapter = adapter

        tvDesignation!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                // TODO Auto-generated method stub
                empDesignation = desMapList!!.get(position)
                for (i in 0..(desMap.size - 1)) {
                    if (empDesignation.equals(desMapList[i], true)) {
                        empDesignation = desMap[i].toString()
                        Log.d("TAG", "response designation Employee" + empDesignation)
                        break
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
    }

    private fun getDepartmentListInAdapter(depMapList: java.util.ArrayList<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, depMapList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tvDepartment!!.adapter = adapter

        tvDepartment!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                // TODO Auto-generated method stub
                empDepartment = depMapList!!.get(position)
                Log.d("TAG", "checking keys" + empDepartment)
                for (i in 0..(depMap.size - 1)) {
                    if (empDepartment.equals(depMapList[i], true)) {
                        empDepartment = depMap[i].toString()
                        Log.d("TAG", "response department Employee" + empDepartment)
                        break
                    }
                }


            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })
    }


    fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    /**api for check user available*/
    fun btnClick_login(view: View) {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        val emailId: String = teDomainId!!.text.toString()
        val passwords: String = loginPassword!!.text.toString()
        if (!isValid(emailId)) {
            hideKeyboard(teDomainId!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_MAIL, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (passwords.equals("", true) || passwords.length < 8) {
            hideKeyboard(loginPassword!!)
            Snackbar.make(llLogin!!, AppConstant.VALID_PASS, Snackbar.LENGTH_SHORT).show()
            return
        }
        AppUtil.showProgress(progressBar)
        GlobalScope.launch {
            GetEmployeeDetail.getEmpDetail(emailId, passwords, applicationContext)
        }
    }

    fun btnClick_signup(view: View) {
        btnSignUpClick = true
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        llLoginNext!!.visibility = View.GONE
        llLogin!!.visibility = View.VISIBLE
        getDepartment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                btnSignUpClick = false
                llLoginNext!!.visibility = View.VISIBLE
                llLogin!!.visibility = View.GONE
                empGender = "Male"
                isfromCamera = false
                thumbnail = null
                employeeId = ""
                employeeMailId = ""
                empl = ArrayList<Employee>()
                email!!.setText("")
                teDomainId!!.setText("")
                loginPassword!!.setText("")
                phone!!.setText("")
                id!!.setText("")
                tvFirstName!!.setText("")
                tvLastName!!.setText("")
                password!!.setText("")
                adhaar!!.setText("")
                empDesignation = ""
                empDepartment = ""
                depMap = ArrayList<Int>()
                desMap = ArrayList<Int>()
                depMapList = ArrayList<String>()
                 desMapList = ArrayList<String>()
                empImage!!.setImageDrawable(resources.getDrawable(R.drawable.user, null))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getEmployeeDetails(json: List<com.attendance.office.volley.detail.Data>) {
        AppUtil.cancelProgress(progressBar)
        GlobalScope.launch {
            try {
                val empId = json[0].emp_id
                val name = json[0].first_name
                var lastname = json[0].last_name
                var phonenumber = json[0].contact
                var adhaarNumber = json[0].aadhar//adhar number required
                var emailId = json[0].email
                val emp: Employee = Employee()
                emp.setEmpName(name + " " + lastname)
                emp.setEmailId(emailId)
                emp.setEmpId(empId)
                emp.setEmpAdhaar(adhaarNumber)
                emp.setEmpPhone(phonenumber.toString())
                emp.setTimeStamp(System.currentTimeMillis().toString())
                emp.setEmpDesignation(json[0].designation_id.toString())
                emp.setEmpDepartment(json[0].department_id.toString())
                emp.setEmpGender(json[0].gender)
                emp.setPassword(loginPassword!!.text.toString())
                if (emp != null) {
                    db!!.employeeDao().deleteAllEmployee()
                    historyID = db!!.employeeDao().insert(emp)
                    Log.d("TAG", "history ID" + historyID)
                }

                isUserAvail()
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun getEmployeeError(json: String) {
        Snackbar.make(llLogin!!, json, Snackbar.LENGTH_SHORT).show()
        AppUtil.cancelProgress(progressBar)
    }

    override fun getDepartmentError(json: String) {
        Snackbar.make(llLogin!!, json, Snackbar.LENGTH_SHORT).show()
        AppUtil.cancelProgress(progressBar)
    }

    override fun getDesignationError(json: String) {
        Snackbar.make(llLogin!!, json, Snackbar.LENGTH_SHORT).show()
        AppUtil.cancelProgress(progressBar)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun success(success: String) {
        AppUtil.cancelProgress(progressBar)
        isUserAvail()
    }

    override fun failure(failure: String) {
        Snackbar.make(llLogin!!, failure, Snackbar.LENGTH_SHORT).show()
        AppUtil.cancelProgress(progressBar)
    }

}