package com.vivokey.sparkactions.domain.models

import androidx.room.TypeConverter
import com.vivokey.sparkactions.domain.models.Action.Companion.toActionTarget

class ActionTargetTypeConverter {

    @TypeConverter
    fun fromActionTarget(target: ActionTarget): String {
        return target.toString()
    }

    @TypeConverter
    fun toActionTarget(targetString: String): ActionTarget? {
        return targetString.toActionTarget()
    }
}