package com.beaterboofs.missionout.Util

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