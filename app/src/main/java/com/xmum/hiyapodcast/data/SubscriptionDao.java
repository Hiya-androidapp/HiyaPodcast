package com.xmum.hiyapodcast.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao{
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private static final String TAG = "SubscriptionDao";
    private final HiyaDbHelper mHiyaDbHelper;
    private ISubDaoCallback mCallback=null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        //db
        mHiyaDbHelper = new HiyaDbHelper(BaseApplication.getAppContex());

    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback=callback;
    }

    @Override
    public void addAlbum(Album album) {
        boolean isAdd=false;
        SQLiteDatabase db=null;
        try{
            db= mHiyaDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues=new ContentValues();
            //封装数据
            contentValues.put(Constant.SUB_COVER_URL, album.getCoverUrlLarge());
            contentValues.put(Constant.SUB_TITLE, album.getAlbumTitle());
            contentValues.put(Constant.SUB_DESCRIPTION, album.getAlbumIntro());
            contentValues.put(Constant.SUB_TRACKS_COUNT, album.getIncludeTrackCount());
            contentValues.put(Constant.SUB_PLAY_COUNT, album.getPlayCount());
            contentValues.put(Constant.SUB_AUTHOR_NAME, album.getAnnouncer().getNickname());
            contentValues.put(Constant.SUB_ALBUM_ID, album.getId());
            //insert data
            db.insert(Constant.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isAdd=true;

        }catch (Exception e)
        {
            e.printStackTrace();
            isAdd=false;
        }
        finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if(mCallback!=null)
            {
                mCallback.onAddResult(isAdd);
            }
        }


    }

    @Override
    public void delAlbum(Album album) {
        boolean isDelete=false;
        SQLiteDatabase db=null;
        try{
            db= mHiyaDbHelper.getWritableDatabase();
            db.beginTransaction();

            int delete = db.delete(Constant.SUB_TB_NAME, Constant.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.d(TAG, "delete -- > " + delete);
            db.setTransactionSuccessful();
            isDelete=true;
        }catch (Exception e)
        {
            e.printStackTrace();
            isDelete=false;
        }
        finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if(mCallback!=null)
            {
                mCallback.onDelResult(isDelete);
            }
        }

    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mHiyaDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constant.SUB_TB_NAME, null, null, null, null, null, "_id desc");
            //封装数据
            while (query.moveToNext()) {
                Album album = new Album();
                //封面图片
                String coverUrl = query.getString(query.getColumnIndex(Constant.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);
                //
                String title = query.getString(query.getColumnIndex(Constant.SUB_TITLE));
                album.setAlbumTitle(title);
                //
                String description = query.getString(query.getColumnIndex(Constant.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                //
                int tracksCount = query.getInt(query.getColumnIndex(Constant.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);
                //
                int playCount = query.getInt(query.getColumnIndex(Constant.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);
                //
                int albumId = query.getInt(query.getColumnIndex(Constant.SUB_ALBUM_ID));
                album.setId(albumId);
                String authorName = query.getString(query.getColumnIndex(Constant.SUB_AUTHOR_NAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);

                result.add(album);
            }
            //inform ui to update
            query.close();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(db!=null)
            {
                db.endTransaction();
                db.close();
            }
            if(mCallback!=null)
            {
                mCallback.onSubListLoaded(result);
            }

        }
    }
}
