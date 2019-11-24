package com.beaterboofs.missionout.util

import android.view.View

object UIUtil {
    fun getVisibility(isVisible:Boolean) : Int{
        if (isVisible){
            return View.VISIBLE
        }
        else{
            return View.GONE
        }
    }
}