//  Copyright (c) 2003-2020 Xsens Technologies B.V. or subsidiaries worldwide.
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification,
//  are permitted provided that the following conditions are met:
//
//  1.      Redistributions of source code must retain the above copyright notice,
//           this list of conditions, and the following disclaimer.
//
//  2.      Redistributions in binary form must reproduce the above copyright notice,
//           this list of conditions, and the following disclaimer in the documentation
//           and/or other materials provided with the distribution.
//
//  3.      Neither the names of the copyright holders nor the names of their contributors
//           may be used to endorse or promote products derived from this software without
//           specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
//  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
//  THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
//  OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
//  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY OR
//  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.THE LAWS OF THE NETHERLANDS
//  SHALL BE EXCLUSIVELY APPLICABLE AND ANY DISPUTES SHALL BE FINALLY SETTLED UNDER THE RULES
//  OF ARBITRATION OF THE INTERNATIONAL CHAMBER OF COMMERCE IN THE HAGUE BY ONE OR MORE
//  ARBITRATORS APPOINTED IN ACCORDANCE WITH SAID RULES.
//

package com.xsens.dot.android.example.apps;

import static com.xsens.dot.android.example.views.DataBaseHelper.CORE_TABLE;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.xsens.dot.android.example.adapters.DataAdapter;
import com.xsens.dot.android.example.views.CoreInfoClass;
import com.xsens.dot.android.example.views.DataBaseHelper;
import com.xsens.dot.android.sdk.XsensDotSdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A customized application class for basic initialization.
 */
public class XsensDotApplication extends Application {

    private static List<CoreInfoClass> coreList = new ArrayList<CoreInfoClass>();
    private static int intId = 1;
    private static final String TAG = XsensDotApplication.class.getSimpleName();

    @Override
    public void onCreate() {

        super.onCreate();

        initXsensDotSdk();
    }

    /**
     * Setup for Xsens DOT SDK.
     */
    private void initXsensDotSdk() {

        // Get the version name of SDK.
        String version = XsensDotSdk.getSdkVersion();
        Log.i(TAG, "initXsensDotSdk() - version: " + version);

        // Enable this feature to monitor logs from SDK.
        XsensDotSdk.setDebugEnabled(true);
        // Enable this feature then SDK will start reconnection when the connection is lost.
        XsensDotSdk.setReconnectEnabled(true);
    }

    public XsensDotApplication() {
        fillCoreInfoList();
    }

    private void fillCoreInfoList() {

        //CoreInfoClass core1 = new CoreInfoClass(0, "fsdf", "fdsf", "fsdf",
          //      "dsfsdf", "fdsfs", "fdsf", "fsdf", "fsdf", "fdsf", "fsdfs");

        coreList.addAll(Arrays.asList(new CoreInfoClass[] {}));
    }

    public static List<CoreInfoClass> getCoreList() {
        return coreList;
    }

    public static void setCoreList(List<CoreInfoClass> coreList) {
        XsensDotApplication.coreList = coreList;
    }

    public static int getIntId() {
        return intId;
    }

    public static void setIntId(int intId) {
        XsensDotApplication.intId = intId;
    }


}
