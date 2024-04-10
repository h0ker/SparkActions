package com.vivokey.sparkactions.domain.models

enum class ActionType(val value: String) {
    URL("Website"),
    PHONE("Phone"),
    EMAIL("Email"),
    SMS("SMS");

    companion object {
        fun fromString(value: String): ActionType? {
            return ActionType.values().find { it.value == value }
        }
    }
}