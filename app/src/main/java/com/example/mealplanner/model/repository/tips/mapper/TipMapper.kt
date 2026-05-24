package com.example.mealplanner.model.repository.tips.mapper

import com.example.mealplanner.model.Tip
import com.example.mealplanner.model.data.remote.dto.RemoteTipDto

/** DTO → Domain. Drops the network-only `userId` field. */
fun RemoteTipDto.toDomain(): Tip = Tip(
    id    = id ?: 0,
    title = title,
    body  = body
)

/** Domain → DTO. Sends the current user's id as the owner. */
fun Tip.toDto(ownerUserId: Int = 1): RemoteTipDto = RemoteTipDto(
    id     = if (id == 0) null else id,
    userId = ownerUserId,
    title  = title,
    body   = body
)
