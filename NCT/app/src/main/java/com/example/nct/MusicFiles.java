package com.example.nct;

public class MusicFiles {

    private Long id;
    private String title;
    private String artist;
    private String album;
    private String fileUrl;

    public MusicFiles() {
    }

    public MusicFiles(Long id,
                      String title,
                      String artist,
                      String album,
                      String fileUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.fileUrl = fileUrl;
    }

    public MusicFiles(String url, String title, String artist, String album, String number, String number1, boolean b) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}