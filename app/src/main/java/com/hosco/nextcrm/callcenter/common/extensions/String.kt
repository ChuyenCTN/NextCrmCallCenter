package com.pacom.lashinbang.common.extensions

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.encodeSH256(): String {
    var md: MessageDigest? = null
    var oauthToken = ""
    try {
        md = MessageDigest.getInstance("SHA-256")
        md!!.update(this.toByteArray(charset("UTF-8")))
        val digest = md.digest()
        oauthToken = String.format("%064x", java.math.BigInteger(1, digest))
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }

    return oauthToken
}