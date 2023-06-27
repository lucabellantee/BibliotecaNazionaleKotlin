package com.example.biblioteca_nazionale.model

import android.os.Parcel
import android.os.Parcelable

data class MiniBook(
    var isbn: String,
    var bookPlace: String,
    var image: String,
    var date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(isbn)
        parcel.writeString(bookPlace)
        parcel.writeString(image)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MiniBook> {
        override fun createFromParcel(parcel: Parcel): MiniBook {
            return MiniBook(parcel)
        }

        override fun newArray(size: Int): Array<MiniBook?> {
            return arrayOfNulls(size)
        }
    }
}

