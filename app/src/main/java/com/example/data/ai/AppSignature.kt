package com.example.data.ai

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest

/** SHA-1 fingerprint of the certificate this APK is signed with (used to lock the API key to this app). */
object AppSignature {
    @Suppress("DEPRECATION")
    fun sha1Hex(context: Context): String {
        return try {
            val pm = context.packageManager
            val signatures: Array<Signature>? = if (Build.VERSION.SDK_INT >= 28) {
                val info = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                info.signingInfo?.apkContentsSigners
            } else {
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures
            }
            val first = signatures?.firstOrNull() ?: return ""
            MessageDigest.getInstance("SHA-1")
                .digest(first.toByteArray())
                .joinToString("") { "%02X".format(it) }
        } catch (_: Exception) {
            ""
        }
    }
}
