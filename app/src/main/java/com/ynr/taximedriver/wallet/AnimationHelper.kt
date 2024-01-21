package com.ynr.taximedriver.wallet

import android.view.View
import android.view.animation.TranslateAnimation

object AnimationHelper {

    fun animate(holder: View) {
        /* val anim = AlphaAnimation(0.0f, 1.0f)
         anim.duration = 1000*/

        val anim = TranslateAnimation(-100f, 0.0f, 0.0f, 0.0f)
        anim.duration = 1000

        holder.startAnimation(anim)
    }
}