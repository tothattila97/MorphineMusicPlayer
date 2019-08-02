package attila.toth.production.morphinemusicplayer.model;


import android.graphics.Bitmap;

public class MusicModel{

    String musicPath;
    String musicName;
    String musicAlbum;
    String musicAlbumArt;
    String musicArtist;
    String musicTitle;
    Bitmap albumArt;
    int musicDuration;

    public MusicModel(){

    }

    public MusicModel(String path, String name, String album, String artist, String title){
        this.musicPath = path;
        this.musicName = name;
        this.musicAlbum = album;
        this.musicArtist = artist;
        this.musicTitle = title;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicAlbum() {
        return musicAlbum;
    }

    public void setMusicAlbum(String musicAlbum) {
        this.musicAlbum = musicAlbum;
    }

    public String getMusicAlbumArt() {return musicAlbumArt;}

    public void setMusicAlbumArt(String musicAlbumArt) {this.musicAlbumArt = musicAlbumArt;}

    public String getMusicArtist() {
        return musicArtist;
    }

    public void setMusicArtist(String musicArtist) {this.musicArtist = musicArtist;}

    public int getMusicDuration() {return musicDuration;}

    public void setMusicDuration(int musicDuration) {this.musicDuration = musicDuration;}

    public String getMusicTitle() {return musicTitle;}

    public void setMusicTitle(String musicTitle) {this.musicTitle = musicTitle;}

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

}
