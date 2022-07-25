package com.attendance.office.ui.info

class FetchInfoModal(hours: String?, atten_date: String?, emp_id: String?, id: String?, name: String?,
                     signin_time: String?, signout_time: String?, mark_in_place: String?, mark_out_place: String?) {
    private var hours: String
    private var atten_date: String
    private var emp_id: String
    private var id: String
    private var name: String
    private var signin_time: String
    private var signout_time: String
    private var mark_in_place:String
    private var mark_out_place:String
    init {
        this.hours = hours!!
        this.atten_date = atten_date!!
        this.emp_id = emp_id!!
        this.id = id!!
        this.name = name!!
        this.signin_time = signin_time!!
        this.signout_time = signout_time!!
        this.mark_in_place = mark_in_place!!
        this.mark_out_place = mark_out_place!!
    }

    fun getHours(): String? {
        return this.hours
    }
    fun setHours(hours: String?) {
        this.hours = hours!!
    }
    fun getAttenDate(): String? {
        return this.atten_date
    }
    fun setAttenDate(atten_date: String?) {
        this.atten_date = atten_date!!
    }
    fun getEmpId(): String? {
        return this.emp_id
    }
    fun setEmpId(emp_id: String?) {
        this.emp_id = emp_id!!
    }
    fun getId(): String? {
        return this.id
    }
    fun setId(id: String?) {
        this.id = id!!
    }
    fun getMarkInPlace(): String? {
        return this.mark_in_place
    }
    fun setMarkInPlace(mark_in_place: String?) {
        this.mark_in_place = mark_in_place!!
    }
    fun getMarkOutPlace(): String? {
        return this.mark_out_place
    }
    fun setMarkOutPlace(mark_out_place: String?) {
        this.mark_out_place = mark_out_place!!
    }
    fun getSignOutTime(): String? {
        return this.signout_time
    }
    fun setSignOutTime(signout_time: String?) {
        this.signout_time = signout_time!!
    }
    fun getSignInTime(): String? {
        return this.signin_time
    }
    fun setSignInTime(signin_time: String?) {
        this.signin_time = signin_time!!
    }
    fun getName(): String? {
        return this.name
    }
    fun setName(name: String?) {
        this.name = name!!
    }



}