package com.example.biblioteca_nazionale.model

data class Users(
    val UID: String ,
    val email: String,
    val userSettings: UserSettings
) {
   override fun toString(): String{
       val uid: String = this.UID
       val email: String = this.email
       val settings: String = this.userSettings.toString()

       return "Uid: " + uid + "Email: " + email + " " + settings
   }

}
