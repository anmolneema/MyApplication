package com.attendance.office.ui.info

class EmployeeModal(
    empName: String?,
    empId: String?,
    phone: String?,
    empMailId: String?,
    adhaar: String?,
    empDepartment: String?,
    empDesignation: String?,
    empGender: String?,
) {
    private var empName: String
    private var empId: String
    private var phone: String
    private var adhaar: String
    private var mailId: String
    private var department: String
    private var designation: String
    private var gender: String

    init {
        this.empName = empName!!
        this.empId = empId!!
        this.phone = phone!!
        this.mailId = empMailId!!
        this.adhaar = adhaar!!
        this.department = empDepartment!!
        this.designation = empDesignation!!
        this.gender = empGender!!
    }

    fun getAdhaar(): String? {
        return adhaar
    }

    fun setAdhaar(name: String?) {
        adhaar = name!!
    }

    fun getEmpName(): String? {
        return empName
    }

    fun setEmpName(name: String?) {
        empName = name!!
    }

    fun getEmpId(): String? {
        return empId
    }

    fun setEmpId(year: String?) {
        this.empId = year!!
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(genre: String?) {
        this.phone = genre!!
    }

    fun getMailId(): String? {
        return mailId
    }

    fun setMailId(markIn: String?) {
        this.mailId = markIn!!
    }

    fun getEmpDepartment(): String? {
        return department
    }

    fun setEmpDepartment(department: String?) {
        this.department = department!!
    }

    fun getEmpDesignation(): String? {
        return designation
    }

    fun setEmpDesignation(design: String?) {
        this.designation = design!!
    }

    fun getEmpGender(): String? {
        return gender
    }

    fun setEmpGender(gender: String?) {
        this.gender = gender!!
    }
}