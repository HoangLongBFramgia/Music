package hoanglong.framgia.com.demomusic.model;

public class Song {
    private int song;
    private String nameSong;
    private int imageSong;
    private String author;

    public Song(int song, String nameSong, int imageSong, String author) {
        this.song = song;
        this.nameSong = nameSong;
        this.imageSong = imageSong;
        this.author = author;
    }

    public int getSong() {
        return song;
    }

    public void setSong(int song) {
        this.song = song;
    }

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public int getImageSong() {
        return imageSong;
    }

    public void setImageSong(int imageSong) {
        this.imageSong = imageSong;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Song{" +
                "song=" + song +
                ", nameSong='" + nameSong + '\'' +
                ", imageSong=" + imageSong +
                '}';
    }
}
