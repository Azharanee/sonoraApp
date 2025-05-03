package com.yourcompany.sonora;

public class MusicFiles {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;


    public MusicFiles(String album,String artist, String duration, String path, String title) {
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.title = title;
        this.artist= artist;
    }

    public MusicFiles() {
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
