package com.mangbaam.remoteconfigservice.data.datasource

import com.mangbaam.remoteconfigservice.data.service.RemoteConfigService
import com.mangbaam.remoteconfigservice.domain.model.Level
import com.mangbaam.remoteconfigservice.domain.model.Skill
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
internal data class SkillDto(
    val name: String,
    val level: Int,
) {
    fun toDomain(): Skill = Skill(name = name, level = Level(level))
}

internal class UserDataSource @Inject constructor(
    private val remoteConfigService: RemoteConfigService,
) {
    suspend fun getNickname(): String = remoteConfigService.getString("nickname", "")
    suspend fun getAge(): Int = remoteConfigService.getLong("age", 0L).toInt()
    suspend fun getIsMarried(): Boolean = remoteConfigService.getBoolean("married", false)
    suspend fun getSkills(): List<Skill> =
        remoteConfigService.getReferenceType<List<SkillDto>>("skills", emptyList()).map(SkillDto::toDomain)
}
