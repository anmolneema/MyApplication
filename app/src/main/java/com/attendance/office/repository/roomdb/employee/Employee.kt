package com.attendance.office.repository.roomdb.employee

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// below line is for setting table name.
@Entity(tableName = "table_employee")
 class Employee {
    // below line is to auto increment
    // id for each course.


    @PrimaryKey(autoGenerate = true)
    private var id :Int= 0

    @ColumnInfo(name = "emp_name")
    private var empName: String=""

    @ColumnInfo(name = "emp_mail")
    private var emailId: String=""

    @ColumnInfo(name = "emp_id")
    private var empId: String=""


    @ColumnInfo(name = "emp_phone")
    private var empPhone: String=""

    @ColumnInfo(name = "emp_adhaar")
    private var empAdhaar: String=""

    @ColumnInfo(name = "emp_pssword")
    private var password: String=""

    @ColumnInfo(name = "timestamp")
    private var timeStamp: String=""

    @ColumnInfo(name = "emp_department")
    private var empDepartment: String=""

    @ColumnInfo(name = "emp_designation")
    private var empDesignation: String=""

    @ColumnInfo(name = "emp_gender")
    private var empGender: String=""
    /*
    * Getters and Setters
    * */
    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getEmpName(): String? {
        return empName
    }

    fun setEmpName(task: String?) {
        this.empName = task!!
    }

    fun getEmailId(): String? {
        return emailId
    }


    fun setEmailId(task: String?) {
        this.emailId = task!!
    }

    fun getEmpId(): String? {
        return empId
    }

    fun setEmpId(desc: String?) {
        this.empId = desc!!
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password!!
    }

    fun getTimeStamp(): String? {
        return timeStamp
    }

    fun setTimeStamp(time: String?) {
        this.timeStamp = time!!
    }

    fun getEmpAdhaar(): String? {
        return empAdhaar
    }

    fun setEmpAdhaar(adhaar: String?) {
        this.empAdhaar = adhaar!!
    }

    fun getEmpPhone(): String? {
        return empPhone
    }

    fun setEmpPhone(phone: String?) {
        this.empPhone = phone!!
    }

    fun getEmpDepartment(): String? {
        return empDepartment
    }

    fun setEmpDepartment(department: String?) {
        this.empDepartment = department!!
    }

    fun getEmpDesignation(): String? {
        return empDesignation
    }

    fun setEmpDesignation(design: String?) {
        this.empDesignation = design!!
    }

    fun getEmpGender(): String? {
        return empGender
    }

    fun setEmpGender(gender: String?) {
        this.empGender = gender!!
    }

    override fun toString(): String {
        return "Employee(id=$id, empName='$empName', emailId='$emailId', empId='$empId', empPhone='$empPhone', empAdhaar='$empAdhaar', password='$password', timeStamp='$timeStamp', empDepartment='$empDepartment', empDesignation='$empDesignation', empGender='$empGender')"
    }


}