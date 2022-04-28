package com.straiberry.android.common.model

// false means it has not showing yet
data class GuideTourStatusModel(
    var homeGuideTour: Boolean = false,
    var checkupGuideTour: Boolean = false,
    var cameraGuideTour: Boolean = false,
    var questionGuideTour: Boolean = false,
    var profileGuideTour: Boolean = false,
    var calenderGuideTour: Boolean = false,
    var createEventGuideTour: Boolean = false
)