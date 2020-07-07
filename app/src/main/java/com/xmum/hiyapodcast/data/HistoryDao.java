package com.xmum.hiyapodcast.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final HiyaDbHelper mDBhelper;
    private IHistoryDaoCallback mCallback = null;
    private Object mLock = new Object();

    public HistoryDao() {
        mDBhelper = new HiyaDbHelper(BaseApplication.getAppContex());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;

    }

    @Override
    public void addHistory(Track track) {
        synchronized(mLock) {
            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mDBhelper.getWritableDatabase();
                //先去删除
                int delResult = db.delete(Constant.HISTORY_TB_NAME,Constant.HISTORY_TRACK_ID + "=?",new String[]{track.getDataId() + ""});
                LogUtil.d(TAG,"delResult -- > " + delResult);
                //删除以后再添加
                db.beginTransaction();
                ContentValues values = new ContentValues();
                //封装数据
                values.put(Constant.HISTORY_TRACK_ID,track.getDataId());
                values.put(Constant.HISTORY_TITLE,track.getTrackTitle());
                values.put(Constant.HISTORY_PLAY_COUNT,track.getPlayCount());
                values.put(Constant.HISTORY_DURATION,track.getDuration());
                values.put(Constant.HISTORY_UPDATE_TIME,track.getUpdatedAt());
                values.put(Constant.HISTORY_COVER,track.getCoverUrlLarge());
                values.put(Constant.HISTORY_AUTHOR,track.getAnnouncer().getNickname());
                //插入数据
                db.insert(Constant.HISTORY_TB_NAME,null,values);
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch(Exception e) {
                isSuccess = false;
                e.printStackTrace();
            } finally {
                if(db != null) {
                    db.endTransaction();
                    db.close();
                }
                if(mCallback != null) {
                    mCallback.onHistoryAdd(isSuccess);
                }
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized(mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mDBhelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constant.HISTORY_TB_NAME,Constant.HISTORY_TRACK_ID + "=?",new String[]{track.getDataId() + ""});
                LogUtil.d(TAG,"delete -- > " + delete);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch(Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if(db != null) {
                    db.endTransaction();
                    db.close();
                }
                if(mCallback != null) {
                    mCallback.onHistoryDel(isDeleteSuccess);
                }
            }
        }
    }

    @Override
    public void cleanHistories() {
        synchronized(mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSuccess = false;
            try {
                db = mDBhelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constant.HISTORY_TB_NAME,null,null);
                db.setTransactionSuccessful();
                isDeleteSuccess = true;
            } catch(Exception e) {
                e.printStackTrace();
                isDeleteSuccess = false;
            } finally {
                if(db != null) {
                    db.endTransaction();
                    db.close();
                }
                if(mCallback != null) {
                    mCallback.onHistoriesClean(isDeleteSuccess);
                }
            }
        }
    }


    @Override
    public void listHistories() {
        synchronized(mLock) {
            //从数据表中查出所有的历史记录
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();
            try {
                db = mDBhelper.getReadableDatabase();
                db.beginTransaction();
                Cursor cursor = db.query(Constant.HISTORY_TB_NAME,null,null,null,null,null,"_id desc");
                while(cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(Constant.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constant.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String cover = cursor.getString(cursor.getColumnIndex(Constant.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlSmall(cover);
                    track.setCoverUrlMiddle(cover);
                    String author = cursor.getString(cursor.getColumnIndex(Constant.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    histories.add(track);
                }
                db.setTransactionSuccessful();
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(db != null) {
                    db.endTransaction();
                    db.close();
                }
                //通知出去
                if(mCallback != null) {
                    mCallback.onHistoriesLoaded(histories);
                }
            }
        }
    }
}
