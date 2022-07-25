package com.attendance.office.ui.info

class InfoModal(date: String?, latlong: String?, address: String?, markIn: String?, signTime:String?, signOutTime:String?) {
    private var dateTime: String
    private var latLongV: String
    private var addresses: String
    private var markIn :String
    private var signTime :String
    private var signOutTime :String
    init {
        this.dateTime = date!!
        this.latLongV = latlong!!
        this.addresses = address!!
        this.markIn = markIn!!
        this.signTime  = signTime!!
        this.signOutTime  = signOutTime!!
    }
    fun getDateTime(): String? {
        return dateTime
    }
    fun setDateTime(name: String?) {
        dateTime = name!!
    }
    fun getLatLongV(): String? {
        return latLongV
    }
    fun setLatLongV(year: String?) {
        this.latLongV = year!!
    }
    fun getAddresses(): String? {
        return addresses
    }
    fun setAddresses(genre: String?) {
        this.addresses = genre!!
    }

    fun getMarkIn(): String? {
        return markIn
    }
    fun setMarkIn(markIn: String?) {
        this.markIn = markIn!!
    }

    fun getSignOutTime(): String? {
        return signOutTime
    }
    fun setSignOutTime(signOutTime: String?) {
        this.signOutTime = signOutTime!!
    }

    fun getSignIn(): String? {
        return signTime
    }
    fun setSignIn(markIn: String?) {
        this.signTime = signTime!!
    }
}