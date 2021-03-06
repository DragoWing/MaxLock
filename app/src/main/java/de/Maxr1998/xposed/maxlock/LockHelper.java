/*
 * MaxLock, an Xposed applock module for Android
 * Copyright (C) 2014-2015  Maxr1998
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.Maxr1998.xposed.maxlock;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class LockHelper {
    public static final String MY_PACKAGE_NAME = LockHelper.class.getPackage().getName();
    private static final String[] ACTIVITIES_NO_UNLOCK = new String[]{
            "com.evernote.ui.HomeActivity",
            "com.fstop.photo.MainActivity",
            "com.instagram",
            "com.twitter.android.StartActivity",
            "com.UCMobile.main.UCMobile",
            "com.whatsapp.Main",
            "cum.whatsfapp.Main",
            "jp.co.johospace.jorte.MainActivity",
            "se.feomedia.quizkampen.act.login.MainActivity"
    };
    public static final Set<String> NO_UNLOCK = new HashSet<>(Arrays.asList(ACTIVITIES_NO_UNLOCK));

    public static void launchLockView(Activity caller, Intent intent, String packageName, String launch) {
        Intent it = new Intent();
        it.setComponent(new ComponentName(MY_PACKAGE_NAME, MY_PACKAGE_NAME + launch));
        it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        it.putExtra(Common.INTENT_EXTRAS_INTENT, intent);
        it.putExtra(Common.INTENT_EXTRAS_PKG_NAME, packageName);
        caller.startActivity(it);
    }

    public static boolean timerOrIMod(String packageName, long unlockTimestamp, SharedPreferences iMod, SharedPreferences iModTemp) {
        // Technical timer
        if (unlockTimestamp != 0 && System.currentTimeMillis() - unlockTimestamp <= 800) {
            return true;
        }

        // Intika I.MoD
        boolean iModDelayGlobalEnabled = iMod.getBoolean(Common.IMOD_DELAY_GLOBAL_ENABLED, false);
        boolean iModDelayAppEnabled = iMod.getBoolean(Common.IMOD_DELAY_APP_ENABLED, false);
        long iModLastUnlockGlobal = iModTemp.getLong(Common.IMOD_LAST_UNLOCK_GLOBAL, 0);
        long iModLastUnlockApp = iModTemp.getLong(packageName + "_imod", 0);

        return (iModDelayGlobalEnabled && (iModLastUnlockGlobal != 0 &&
                System.currentTimeMillis() - iModLastUnlockGlobal <=
                        iMod.getInt(Common.IMOD_DELAY_GLOBAL, 600000)))
                ||/* Per app */(iModDelayAppEnabled) && (iModLastUnlockApp != 0 &&
                System.currentTimeMillis() - iModLastUnlockApp <=
                        iMod.getInt(Common.IMOD_DELAY_APP, 600000));
    }
}
