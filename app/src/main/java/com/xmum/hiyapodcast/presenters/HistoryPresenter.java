package com.xmum.hiyapodcast.presenters;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.data.HistoryDao;
import com.xmum.hiyapodcast.data.IHistoryDao;
import com.xmum.hiyapodcast.data.IHistoryDaoCallback;
import com.xmum.hiyapodcast.interfaces.IHistoryCallback;
import com.xmum.hiyapodcast.interfaces.IHistoryPresenter;
import com.xmum.hiyapodcast.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {
    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;
    private static HistoryPresenter sHistoryPresenter = null;
    private final IHistoryDao mHistoryDao;
    private boolean isDoDelAsOutOfSize = false;
    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    public static HistoryPresenter getHistoryPresenter() {
        if(sHistoryPresenter == null) {
            synchronized(HistoryPresenter.class) {
                if(sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }
    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addHistory(final Track track) {
        if (mCurrentHistories !=null&& mCurrentHistories.size()>= Constant.MAX_HISTORY_COUNT){
            isDoDelAsOutOfSize = true;
            this.mCurrentAddTrack = track;
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        }else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.cleanHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }



    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if(isDoDelAsOutOfSize && mCurrentAddTrack != null) {
            isDoDelAsOutOfSize = false;
            //添加当前的数据进到数据库里
            addHistory(mCurrentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {
        this.mCurrentHistories = tracks;
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for(IHistoryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if(!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        //delete ui callback
        mCallbacks.remove(iHistoryCallback);
    }
}
