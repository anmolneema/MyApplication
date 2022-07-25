package com.attendance.office

import android.content.Intent
import android.location.*
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.attendance.office.repository.roomdb.employee.Employee
import com.attendance.office.repository.roomdb.employee.EmployeeDataBase
import com.attendance.office.tensorflow.lite.examples.detection.DetectorActivity
import com.attendance.office.ui.home.HomeFragment
import com.attendance.office.ui.info.EmployeeModal
import com.attendance.office.ui.info.InfoFragment
import com.attendance.office.ui.info.InfoModal
import com.attendance.office.ui.logout.LogoutFragment
import com.attendance.office.ui.profile.ProfileFragment
import com.attendance.office.volley.detail.GetEmployeeMarkedDetails
import com.attendance.office.volley.markinout.MarkInOut
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.splash_screen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity





class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    private  var employeeList: List<Employee>?=ArrayList<Employee>()
    private  var empList: ArrayList<EmployeeModal>?=ArrayList<EmployeeModal>()
    private var navView: NavigationView?=null
    var isHomeFragment: Boolean = true
    private var isDrawerOpen: Boolean = false
    private var backTimer: CountDownTimer? = null
    var db: EmployeeDataBase? = null
    var drawerLayout: DrawerLayout? = null
    var latLng: LatLng? = null
     var doubleBackToExitPressedOnce = false
     val mHandler: Handler = Handler()
    var  empName :String =""
    var empMail: String = ""
    var empId: String = ""
    var empPhone: String = ""
    var empAdhaar: String = ""
    var empDesignation: String = ""
    var empDepartment: String = ""
    var empGender: String = "Male"
    var welComeName: TextView?=null

    private lateinit var appBarConfiguration: AppBarConfiguration



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        intentValue();
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout!!.addDrawerListener(this)
         navView = findViewById(R.id.nav_view)
        val headerView: View = navView!!.getHeaderView(0)
        welComeName = headerView!!.findViewById(R.id.empWelcome)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_info, R.id.nav_logout
            ), drawerLayout
        )
        navView!!.setNavigationItemSelectedListener(this)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView!!.setupWithNavController(navController)
        getEmployeeValue()

    }

    private fun getEmployeeValue() {
        if(db!=null){
            GlobalScope.launch {
                employeeList = db!!.employeeDao().getLastEmp()
                if (employeeList != null && employeeList?.size!! > 0) {
                    employeeList!!.forEach {

                        var empModal = EmployeeModal(it!!.getEmpName(),it!!.getEmpId(),it!!.getEmpPhone(),it!!.getEmailId(), it.getEmpAdhaar(), it.getEmpDepartment(), it.getEmpDesignation(), it.getEmpGender())
                        empList!!.add(empModal)
                        empName = empModal.getEmpName()!!
                        empId= empModal.getEmpId()!!
                        empMail = empModal.getMailId()!!
                        empPhone = empModal.getPhone()!!
                        empAdhaar = empModal.getAdhaar()!!
                        empDepartment = empModal.getEmpDepartment()!!
                        empDesignation = empModal.getEmpDesignation()!!
                        empGender = empModal.getEmpGender()!!
                       welComeName!!.text = empName
                    }

                } else {

                }
            }

        }
    }


    private fun intentValue() {
        db = Room.databaseBuilder(applicationContext, EmployeeDataBase::class.java, "employee.db")
            .build()
        var intent = intent;

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
         if(isDrawerOpen && isHomeFragment){
            isDrawerOpen = false
            drawerLayout!!.closeDrawer(GravityCompat.START)
             return
        }
        else if (isDrawerOpen || !isHomeFragment) {

//            openHomeFragment()
//            return
             if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                 isDrawerOpen = false
                 navView!!.setCheckedItem(R.id.nav_home);
                 navView!!.getMenu().performIdentifierAction(R.id.nav_home, 0);
             }
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return;
            }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, AppConstant.PRESS_ONCE, Toast.LENGTH_SHORT).show();

                mHandler.postDelayed(mRunnable, 2000);
            }
    }

    private val mRunnable =
        Runnable { doubleBackToExitPressedOnce = false }


    private fun openHomeFragment() {
        displaySelectedScreen(R.id.nav_home)
    }

    private fun stopTimer() {

        if (backTimer != null) {
            backTimer!!.cancel()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        displaySelectedScreen(id);
        return true
    }

    private fun displaySelectedScreen(id: Int) {
        var fragment: Fragment? = null

        //initializing the fragment object which is selected
        when (id) {
            R.id.nav_home -> {isHomeFragment= true
                fragment = HomeFragment()}
            R.id.nav_profile -> {
                isHomeFragment =false
                fragment = ProfileFragment()}
            R.id.nav_info -> {
                isHomeFragment =false
                fragment = InfoFragment()}
            R.id.nav_logout ->{
                isHomeFragment =false
                fragment = LogoutFragment()}
        }

        //replacing the fragment

        //replacing the fragment
        if (fragment != null) {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.nav_host_fragment, fragment)
            ft.commit()
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
    }

    fun openLogin() {
        val intent = Intent(this, SplashScreen::class.java)
        intent.putExtra(AppConstant.LOGOUT, true)
        finish()
        startActivity(intent)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerOpened(drawerView: View) {
        isDrawerOpen = true
    }

    override fun onDrawerClosed(drawerView: View) {
        isDrawerOpen = false
    }

    override fun onDrawerStateChanged(newState: Int) {
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    fun openFaceActivity() {
        val intent = Intent(this, DetectorActivity::class.java)
        startActivity(intent!!)
    }


}