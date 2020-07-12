package com.xmum.hiyapodcast.utils;

public class Constant {
    public static final int MAX_SUB_COUNT = 100;
    //number of recommendation
    public static int COUNT_RECOMMAND =50;

    //number of default request in the list
    public static int COUNT_DEFAULT=50;

    //number of hot words
    public static int COUNT_HOT_WORDS=10;

    //db constant
    public static final String DB_NAME="Hiya.db";
    public static final int DB_VERSION_CODE=1;
    //for subscription
    public static final String SUB_TB_NAME = "tb_subscription";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "coverUrl";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_TRACKS_COUNT = "tracksCount";
    public static final String SUB_PLAY_COUNT = "playCount";
    public static final String SUB_AUTHOR_NAME = "authorName";
    public static final String SUB_ALBUM_ID = "albumId";
    //name for history
    public static final String HISTORY_TB_NAME = "tb_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TRACK_ID = "historyTrackId";
    public static final String HISTORY_TITLE = "historyTitle";
    public static final String HISTORY_PLAY_COUNT = "historyPlayCount";
    public static final String HISTORY_DURATION = "historyDuration";
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";
    public static final String HISTORY_COVER = "historyCover";
    public static final String HISTORY_AUTHOR = "history_author";
    //largest history number
    public static final int MAX_HISTORY_COUNT = 100;


}
