package cn.jamesli.example.bt03easycursortest.container;

import uk.co.alt236.easycursor.EasyCursor;

/**
 * Created by jamesli on 15-8-13.
 */
public class TrackInfo {
    private final long m_id;
    private final String mArtist;
    private final String mAlbum;
    private final String mTrack;
    private final String mMedia;
    private final String mComposer;
    private final boolean mHasComposer;
    private final long mMeaninglessSum;
    private final double mMeaninglessDiv;

    public TrackInfo(final EasyCursor c) {
        m_id = c.getLong("_id");
        mArtist = c.getString("artist");
        mAlbum = c.getString("album");
        mTrack = c.getString("track");
        mMedia = c.getString("media");
        mComposer = c.getString("composer");
        mHasComposer = c.getBoolean("hascomposer");
        mMeaninglessSum = c.getLong("meaninglessSum");
        mMeaninglessDiv = c.getDouble("meaninglessDiv");
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getComposer() {
        return mComposer;
    }

    public double getMeaninglessDiv() {
        return mMeaninglessDiv;
    }

    public long getMeaninglessSum() {
        return mMeaninglessSum;
    }

    public String getMedia() {
        return mMedia;
    }

    public String getTrack() {
        return mTrack;
    }

    public long get_id() {
        return m_id;
    }

    public boolean isHasComposer() {
        return mHasComposer;
    }
}
