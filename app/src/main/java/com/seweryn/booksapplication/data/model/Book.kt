package com.seweryn.booksapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Book(
    @PrimaryKey @SerializedName("Id") val id: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String?,
    @SerializedName("CoverUrl") val coverUrl: String?)