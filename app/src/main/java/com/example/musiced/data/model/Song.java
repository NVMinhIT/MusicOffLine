package com.example.musiced.data.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Parcelable {
    private int SongId;
    private String SongName;
    private String SingerName;
    private String pathSong;
    private int DurationSong;

    public Song() {

    }

    public Song(int songId, String songName, String singerName, String pathSong, int durationSong) {
        SongId = songId;
        SongName = songName;
        SingerName = singerName;
        this.pathSong = pathSong;
        DurationSong = durationSong;
    }

    protected Song(Parcel in) {
        SongId = in.readInt();
        SongName = in.readString();
        SingerName = in.readString();
        pathSong = in.readString();
        DurationSong = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public int getSongId() {
        return SongId;
    }

    public void setSongId(int songId) {
        SongId = songId;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getSingerName() {
        return SingerName;
    }

    public void setSingerName(String singerName) {
        SingerName = singerName;
    }

    public String getPathSong() {
        return pathSong;
    }

    public void setPathSong(String pathSong) {
        this.pathSong = pathSong;
    }

    public int getDurationSong() {
        return DurationSong;
    }

    public void setDurationSong(int durationSong) {
        DurationSong = durationSong;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(SongId);
        dest.writeString(SongName);
        dest.writeString(SingerName);
        dest.writeString(pathSong);
        dest.writeInt(DurationSong);
    }
}
