package com.example.mealplanner.model

/**
 * Domain model for a cooking / nutrition tip pulled from the remote API.
 * Decoupled from the wire format (RemoteTipDto) so UI/ViewModels never see network-shaped data.
 */
data class Tip(
    val id: Int,
    val title: String,
    val body: String
)
