package com.hosco.nextcrm.callcenter.common


class Const {
    companion object {
        val KEY_EXTRA_DATA: String = "data"
        val KEY_USER: String = "KEY_USER"
        val KEY_RELOAD_LIST_NOTE: String = "reload_list_note"
        const val DEBUG = true
        const val REQUEST_TIMEOUT_DURATION = 10
        val TYPE_POPULAR = 1
        val TYPE_TEXT_POPULAR = 3
        val TYPE_NORMAL = 0
        val TYPE_NOWPLAYING = 2
        val TYPE_TEXT_NOWPLAYING = 4
        val TYPE_MOVIE = 5
        val TYPE_TEXTVIEW = 6
        val DATA_DOMAIN = "intent_data_domain"
        val DATA_PHONE = "intent_data_phone"
        val DATA_CONTACT_RESULT = "data_contact_result"
        val IS_RELOAD = "RELOADD"
        val PAGE_LIMIT = 10

        val TYPE_UDP_SIP = "udp"
        val TYPE_TCP_SIP = ""
        val TYPE_TLS_SIP = ""

        val SIP_AVAILABLE = 11
        val SIP_PROGRESS = 12
        val SIP_DISABLE = 13
        val SIP_DELETE = 14
    }
}