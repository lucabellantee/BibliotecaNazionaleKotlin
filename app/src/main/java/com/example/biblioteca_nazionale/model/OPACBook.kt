package com.example.biblioteca_nazionale.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class OPACBook(
    @SerializedName("codiceIdentificativo") val codiceIdentificativo: String,
)
