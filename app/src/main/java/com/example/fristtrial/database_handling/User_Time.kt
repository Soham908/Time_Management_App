package com.example.fristtrial.database_handling

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User_Time(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "label") val label: String?

)
{
    constructor(time: String?, label: String?): this(0, time, label)
}
