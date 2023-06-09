package com.example.biblioteca_nazionale.model

import com.google.gson.annotations.SerializedName


data class OPACResponse(
    @SerializedName("briefRecords") val briefRecords: List<OPACBook>
)
