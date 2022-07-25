package com.attendance.office

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.attendance.office.AppConstant
import com.attendance.office.R
import com.attendance.office.ui.info.InfoModal
import java.lang.Exception

internal class InfoAdapter(
    private var employeeList: List<InfoModal>,
    private var context: Context
) :
    RecyclerView.Adapter<InfoAdapter.MyViewHolder>() {
    internal class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvDate: TextView = view.findViewById(R.id.tv_ifo_date)
        var tvLatLong: TextView = view.findViewById(R.id.tv_ino_latlong)
        var tvAddress: TextView = view.findViewById(R.id.tv_info_address)
        var ivInfoMark: ImageView = view.findViewById(R.id.iv_info_mark)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.info_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val employee = employeeList[position]
            if (AppConstant.MARKIN.equals(employee.getMarkIn(), true)) {
                holder.ivInfoMark.setImageDrawable(context.resources.getDrawable(R.drawable.check))
                holder.tvDate.text = employee.getDateTime().toString().split(" ")[0]+" "+employee.getSignIn()
            } else {
                holder.ivInfoMark.setImageDrawable(context.resources.getDrawable(R.drawable.check_out))
                holder.tvDate.text = employee.getDateTime().toString().split(" ")[0]+" "+employee.getSignOutTime()
            }
            holder.tvLatLong.text = employee.getLatLongV()
            holder.tvAddress.text = employee.getAddresses()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }
}