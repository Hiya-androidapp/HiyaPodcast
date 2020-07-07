package com.xmum.hiyapodcast.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;

public class HiyaDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    public HiyaDbHelper(Context context) {

        super(context, Constant.DB_NAME, null, Constant.DB_VERSION_CODE);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG,"onCreate...");


        //create db
        String subTbSql = "create table " + Constant.SUB_TB_NAME + "(" +
                Constant.SUB_ID + " integer primary key autoincrement, " +
                Constant.SUB_COVER_URL + " varchar, " +
                Constant.SUB_TITLE + " varchar," +
                Constant.SUB_DESCRIPTION + " varchar," +
                Constant.SUB_PLAY_COUNT + " integer," +
                Constant.SUB_TRACKS_COUNT + " integer," +
                Constant.SUB_AUTHOR_NAME + " varchar," +
                Constant.SUB_ALBUM_ID + " integer" +
                ")";
        db.execSQL(subTbSql);
        //Create history
        String historyTbSql = "create table " + Constant.HISTORY_TB_NAME + "(" +
                Constant.HISTORY_ID + " integer primary key autoincrement, " +
                Constant.HISTORY_TRACK_ID + " integer, " +
                Constant.HISTORY_TITLE + " varchar," +
                Constant.HISTORY_COVER + " varchar," +
                Constant.HISTORY_PLAY_COUNT + " integer," +
                Constant.HISTORY_DURATION + " integer," +
                Constant.HISTORY_AUTHOR + " varchar," +
                Constant.HISTORY_UPDATE_TIME + " integer" +
                ")";
        db.execSQL(historyTbSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
