package dev.cirimo.trosko

import android.Manifest
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestedPermissionsTest {
    /**
     * The promise that the user's financial data stays on the device rests on the app being
     * unable to reach the network at all. A library can add the permission through manifest
     * merging without anyone noticing, so this checks the installed app, not our own manifest.
     */
    @Test
    fun appDoesNotRequestInternetAccess() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val requested =
            context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                .requestedPermissions
                .orEmpty()

        assertFalse(Manifest.permission.INTERNET in requested)
    }
}
