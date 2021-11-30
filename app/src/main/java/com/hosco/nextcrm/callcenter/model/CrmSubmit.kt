package com.hosco.nextcrm.callcenter.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CrmSubmit(
    val isSubmit:Boolean
){}
