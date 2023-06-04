package com.example.biblioteca_nazionale.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class InfoBook(
    @SerializedName("title")val title: String,
    @SerializedName("authors")val authors: List<String>,
    @SerializedName("description")val description: String,
    @SerializedName("publisher")val publisher: String,
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("imageLinks") val imageLinks: ImageLinks
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(ImageLinks::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeStringList(authors)
        parcel.writeString(description)
        parcel.writeString(publisher)
        parcel.writeString(publishedDate)
        parcel.writeParcelable(imageLinks, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InfoBook> {
        override fun createFromParcel(parcel: Parcel): InfoBook {
            return InfoBook(parcel)
        }

        override fun newArray(size: Int): Array<InfoBook?> {
            return arrayOfNulls(size)
        }
    }
}

data class ImageLinks(
    @SerializedName("smallThumbnail") val smallThumbnail: String,
    @SerializedName("thumbnail") val thumbnail: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(smallThumbnail)
        parcel.writeString(thumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageLinks> {
        override fun createFromParcel(parcel: Parcel): ImageLinks {
            return ImageLinks(parcel)
        }

        override fun newArray(size: Int): Array<ImageLinks?> {
            return arrayOfNulls(size)
        }
    }
}
