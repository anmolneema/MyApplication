package com.attendance.office.repository.roomdb.employee

import android.content.Context
import android.os.AsyncTask
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


// adding annotation for our database entities and db version.
@Database(entities = [Employee::class, Info::class], version = 1)
abstract class EmployeeDataBase : RoomDatabase() {
    // below line is to create
    // abstract variable for dao.
    abstract fun employeeDao(): EmployeeDao
    abstract fun infoDao(): InfoDao
}