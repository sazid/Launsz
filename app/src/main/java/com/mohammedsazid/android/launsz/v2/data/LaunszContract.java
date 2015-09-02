/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mohammed Sazid-Al-Rashid
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mohammedsazid.android.launsz.v2.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class LaunszContract {

    public static class AppsInfo implements BaseColumns {

        // Name of the table
        public static final String TABLE_NAME = "apps_info";

        // Column names
        /**
         * Package name of the app
         */
        public static final String COLUMN_APP_PACKAGE_NAME = "package_name";

        /**
         * Name (label) of the app (as seen in the app drawer)
         */
        public static final String COLUMN_APP_LABEL = "label";

        /**
         * Launch count of the app (how many times the app was launched)
         */
        public static final String COLUMN_LAUNCH_COUNT = "launch_count";

        // SQL query for creating the table
        public static final String DATABASE_CREATE_SQL =
                "CREATE TABLE " + TABLE_NAME +
                    " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_APP_PACKAGE_NAME + " TEXT NOT NULL, "
                    + COLUMN_APP_LABEL + " TEXT NOT NULL, "
                    + COLUMN_LAUNCH_COUNT + " INTEGER NOT NULL"
                    + ")" +
                    ";";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE_SQL);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            // For now, drop the table and create a new blank one
            // TODO: Do not drop the table (users will certainly get angry)
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }

    }

}
