package com.attendance.office.ui.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.attendance.office.*
import com.attendance.office.InfoAdapter
import com.attendance.office.repository.roomdb.employee.Info
import com.attendance.office.volley.detail.GetEmployeeMarkedDetails
import com.attendance.office.volley.markinout.Data
import com.attendance.office.volley.markinout.EmployeeMarkedDetail
import com.attendance.office.volley.markinout.MarkInOut
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.lang.Exception

class InfoFragment : Fragment(), GetEmployeeMarkedDetails.MarkedDetails {


    private var infoList: List<Info> = ArrayList<Info>()
    private val employeeList: ArrayList<InfoModal> = ArrayList<InfoModal>()
    private val nonDbemployeeList: ArrayList<InfoModal> = ArrayList<InfoModal>()
    private var recyclerView: RecyclerView? = null
    private lateinit var infoAdapter: InfoAdapter
    private var clInfo: RelativeLayout? = null
    private var attendaceDate: String = ""
    private var signIN: String = ""
    private var signOut: String = ""
    private var hours: String = ""
    private var name: String = ""
    private var markInPlace: String = ""
    private var markOutPlace: String = ""
    private var rlInfo: RelativeLayout? = null
    private var progressBar: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        galleryViewModel =
//                ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_info, container, false)
//        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        GetEmployeeMarkedDetails.setCallBack(this)
        initView(root)
        getMarkInReport()
        //initAdapter()
        return root
    }

    private fun initView(root: View?) {
        ((activity as MainActivity).isHomeFragment) = false
        recyclerView = root?.findViewById(R.id.rv_info)
        clInfo = root?.findViewById(R.id.clInfo);
        rlInfo = root?.findViewById(R.id.llNoInfo)
        progressBar = root?.findViewById(R.id.progressBar)
    }

    private fun getEmployeeInfoListFromDB() {
        try {
            if (((activity as MainActivity)).db != null) {
                GlobalScope.launch {
                    infoList = (activity as MainActivity).db!!.infoDao().getAllInfo()
                    if (infoList != null && infoList?.size!! > 0) {
                        infoList!!.forEach {

                            var infoModal = InfoModal(
                                it!!.getTime(),
                                it!!.getLat() + "," + it!!.getLon(),
                                it!!.getAddress(),
                                it.getMark(),"",""
                            )
                            employeeList!!.add(infoModal)
                            Log.d("Fetch Records", "Id:  : ${it!!.getTime()}")
                            //initAdapter()
                        }

                    } else {

                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun initAdapter() {
        try {
            if (employeeList != null && employeeList.size > 0) {
                infoAdapter = InfoAdapter(employeeList!!, requireContext())
                val layoutManager = LinearLayoutManager(requireContext())
                recyclerView!!.layoutManager = layoutManager
                //  recyclerView!!.itemAnimator = DefaultItemAnimator()
                recyclerView!!.adapter = infoAdapter!!
            } else {
                if (nonDbemployeeList != null && nonDbemployeeList.size > 0) {
                    infoAdapter = InfoAdapter(nonDbemployeeList!!, requireContext())
                    val layoutManager = LinearLayoutManager(requireContext())
                    recyclerView!!.layoutManager = layoutManager
                    //  recyclerView!!.itemAnimator = DefaultItemAnimator()
                    recyclerView!!.adapter = infoAdapter!!
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        // infoAdapter!!.notifyDataSetChanged()
    }

    private fun getMarkInReport() {
        AppUtil.showProgress(progressBar)
        GlobalScope.launch {

            GetEmployeeMarkedDetails.getMarkedDetails(
                ((activity as MainActivity).empId),
                requireContext()
            )
        }
    }

    override fun getAllMarkedDetails(json: List<Data>) {
        AppUtil.cancelProgress(progressBar)
        if (json != null && json.size>0) {
            json.forEach {
                attendaceDate = it.create_at.toString()!!
                signIN = it.in_time.toString()!!
                signOut = it.out_time.toString()
                if(it.working_hour!=null) {
                    hours = it.working_hour.toString()
                }else{
                    hours = "00:00"
                }
            //    name = it.name.toString()
                if(it.log!=null && it.log.size>0){
                    for(i in 0..(it.log.size-1)){
                        if(AppConstant.PUNCH_IN.equals(it.log[i].type.toString(), true)){
                            markInPlace = it.log[i].address
                            val singInLatLong =it.log[i].latlong
                            //markInPlace.split("**")[1] + " " + markInPlace.split("**")[2]
                            val infoModal = InfoModal(
                                attendaceDate,
                                singInLatLong,
                                markInPlace,
                                AppConstant.MARKIN,
                                signIN,
                                signOut
                            )
                            employeeList.add(infoModal)
                          //  markOutPlace =""
                        }else{
                          //  markInPlace =""
                            markOutPlace =  it.log[i].address
                            val singInLatLong =it.log[i].latlong
                            // markOutPlace.split("**")[1] + " " + markOutPlace.split("**")[2]
                            val infoModal = InfoModal(
                                attendaceDate,
                                singInLatLong,
                                markOutPlace,
                                AppConstant.MARKOUT,
                                signIN,
                                signOut
                            )
                            employeeList.add(infoModal)
                        }
                    }
                }
//                if (!markInPlace.equals(AppConstant.BLANK, true)) {
//                    val singInLatLong =markInPlace
//                        //markInPlace.split("**")[1] + " " + markInPlace.split("**")[2]
//                    val infoModal = InfoModal(
//                        attendaceDate,
//                        singInLatLong,
//                        markInPlace.split("**")[0],
//                        AppConstant.MARKIN,
//                        signIN,
//                        signOut
//                    )
//                    employeeList.add(infoModal)
//                 //   nonDbemployeeList.add(infoModal)
//                }
//                if (!markOutPlace.equals(AppConstant.BLANK, true)) {
//                    val singInLatLong =markOutPlace
//                       // markOutPlace.split("**")[1] + " " + markOutPlace.split("**")[2]
//                    val infoModal = InfoModal(
//                        attendaceDate,
//                        singInLatLong,
//                        markOutPlace.split("**")[0],
//                        AppConstant.MARKOUT,
//                        signIN,
//                        signOut
//                    )
//                    employeeList.add(infoModal)
//                 //   nonDbemployeeList.add(infoModal)
//                }
            }
        }
       // getEmployeeInfoListFromDB()
        if (!AppConstant.BLANK.equals(signIN)) {
            rlInfo!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
            initAdapter()
        } else {
            recyclerView!!.visibility = View.GONE
            rlInfo!!.visibility = View.VISIBLE
        }
    }


    override fun getAllMarkedError(failure: String) {
        AppUtil.cancelProgress(progressBar)
        Snackbar.make(clInfo!!, failure, Snackbar.LENGTH_SHORT).show()
    }
}