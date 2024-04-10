package com.vivokey.sparkactions.domain.models.response

import androidx.compose.material3.MaterialTheme
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.Action.Companion.toActionTarget

data class GetRedirectResponse(
    val target: String,
    val title: String,
    val delay: Int,
    val count: Int,
    val lastSeen: String,
    val aj: Boolean,
    val result: String
) {
    fun toAction(): Action? {

        if (this.target == "null") {
            return null
        }
        val actionTarget = this.target.toActionTarget()
        return if (actionTarget == null) {
            null
        } else {
            Action(
                target = actionTarget,
                title = this.title,
                delay = this.delay,
                count = this.count,
                aj = this.aj,
                lastSeen = this.lastSeen
            )
        }
    }
}
