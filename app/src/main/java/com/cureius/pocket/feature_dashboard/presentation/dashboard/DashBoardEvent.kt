package com.cureius.pocket.feature_dashboard.presentation.dashboard

import com.cureius.pocket.feature_account.presentation.add_account.AddAccountEvent

sealed class DashBoardEvent {
    object ToggleInfoSectionVisibility : DashBoardEvent()
}