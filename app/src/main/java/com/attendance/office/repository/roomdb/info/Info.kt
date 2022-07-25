package com.attendance.office.repository.roomdb.employee

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// below line is for setting table name.
@Entity(tableName = "table_info")
 class Info {
    // below line is to auto increment
    // id for each course.


    @PrimaryKey(autoGenerate = true)
    private var id :Int= 0

    @ColumnInfo(name = "info_time")
    private var time: String=""

    @ColumnInfo(name = "info_lat")
    private var lat: String=""

    @ColumnInfo(name = "info_long")
    private var lon: String=""

    @ColumnInfo(name = "info_address")
    private var address: String=""

    @ColumnInfo(name = "info_mark")
    private var mark: String=""
    /*
    * Getters and Setters
    * */
    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTime(): String? {
        return time
    }

    fun setTime(task: String?) {
        this.time = task!!
    }

    fun getLat(): String? {
        return lat
    }

    fun setLat(desc: String?) {
        this.lat = desc!!
    }

    fun getLon(): String? {
        return lon
    }

    fun setLon(lon: String?) {
        this.lon = lon!!
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address!!
    }

    fun getMark(): String? {
        return mark
    }

    fun setMark(mark: String?) {
        this.mark = mark!!
    }
}