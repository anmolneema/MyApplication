package com.attendance.office.volley.markinout

data class Data(
    val create_at: String,
    val createdby: Int,
    val id: Int,
    val in_time: String,
    val log: List<Log>,
    val modifiedby: Int,
    val note: String,
    val out_time: String,
    val status: String,
    val update_at: String,
    val userId: Int,
    val working_hour: String
)