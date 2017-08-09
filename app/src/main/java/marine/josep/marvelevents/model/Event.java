package marine.josep.marvelevents.model;

import android.graphics.Bitmap;

import java.io.Serializable;

import marine.josep.marvelevents.annotation.SQLForeignKey;
import marine.josep.marvelevents.annotation.SQLPrimaryKey;

public class Event implements Serializable{

    @SQLPrimaryKey
    private Integer id;

    private String title;

    private String description;

    @SQLForeignKey(table = "Thumbnail", idType = SQLForeignKey.IdType.STRING)
    private Thumbnail thumbnail;

    private Bitmap thumbBitmap;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return id != null ? id.equals(event.id) : event.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
