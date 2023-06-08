package com.example.biblioteca_nazionale.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView


class CustomMapView : MapView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context,
        attrs!!, defStyle)
    constructor(context: Context, options: GoogleMapOptions?) : super(context, options)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        /**
         * Richiede a tutti i genitori di rinunciare agli eventi di tocco
         */
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }
}
