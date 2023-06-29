package com.example.biblioteca_nazionale.model

import android.os.Parcel
import android.os.Parcelable

data class Review(
    var idComment: String,
    var reviewText: String,
    var reviewTitle: String,
    var isbn: String,
    var vote: Float,
    var date: String,
    var title: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readFloat(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idComment)
        parcel.writeString(reviewText)
        parcel.writeString(reviewTitle)
        parcel.writeString(isbn)
        parcel.writeFloat(vote)
        parcel.writeString(date)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}

