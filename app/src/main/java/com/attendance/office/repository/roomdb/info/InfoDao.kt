package com.attendance.office.repository.roomdb.employee

import androidx.lifecycle.LiveData
import androidx.room.*


// Adding annotation
// to our Dao class
@Dao
interface InfoDao {
    // below method is use to update
    // the data in our database.
    @Update
    fun update(model: Info)

    // below method is use to
    // add data to database.
    @Insert
    fun insert(model: Info):Long

    // below line is use to delete a
    // specific course in our database.
    @Delete
    fun delete(model: Info)

    // on below line we are making query to
    // delete all courses from our database.
    @Query("DELETE FROM table_info")
    fun deleteAllInfo()

    // below line is to read all the courses from our database.
    // in this we are ordering our courses in ascending order
    // with our course name.
    @Query(value = "Select * from table_info")
    fun getAllInfo() : List<Info>


}