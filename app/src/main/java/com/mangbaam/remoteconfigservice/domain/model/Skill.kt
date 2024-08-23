package com.mangbaam.remoteconfigservice.domain.model

import androidx.annotation.IntRange

data class Skill(
    val name: String,
    val level: Level,
)

@JvmInline
value class Level(@IntRange(from = 0, to = 5) val level: Int) {
    override fun toString(): String = when (level.coerceIn(0..5)) {
        0 -> "NoExperience"
        in 1..2 -> "Low"
        3 -> "Medium"
        4 -> "High"
        5 -> "Expert"
        else -> ""
    }
}
