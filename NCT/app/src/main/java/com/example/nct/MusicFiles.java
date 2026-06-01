package com.example.nct;

public class MusicFiles {

    private Long id;

    private String title;

    private String artist;

    private String album;


    private String fileUrl;


    private String path;

    private long albumId;
    private boolean isFavorite;

    // Constructor rỗng
    public MusicFiles() {
    }

    // Constructor cho nhạc online

    public MusicFiles(Long id, String title, String artist, String album, String fileUrl, String path, long albumId, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.fileUrl = fileUrl;
        this.path = path;
        this.albumId = albumId;
        this.isFavorite = isFavorite;
    }
    public MusicFiles(String path, String title, String artist, String album, long albumId, boolean isFavorite) {

        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.isFavorite=isFavorite;
    }


    public MusicFiles(Long id, String title, String artist, String album, String fileUrl, String path, long albumId) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.fileUrl = fileUrl;
        this.path = path;
        this.albumId = albumId;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}