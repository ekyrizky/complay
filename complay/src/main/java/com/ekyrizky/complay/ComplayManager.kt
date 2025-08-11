package com.ekyrizky.complay

import android.content.Context
import android.content.Intent
import com.ekyrizky.complay.ui.ComplayActivity

object ComplayManager {

    fun initialize(context: Context) {

    }

    fun launchPlayer(context: Context) {
        Intent(context, ComplayActivity::class.java).also { context.startActivity(it) }
    }
}