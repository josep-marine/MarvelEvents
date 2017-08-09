package marine.josep.marvelevents.model;

import marine.josep.marvelevents.annotation.SQLPrimaryKey;

public class Thumbnail {

    @SQLPrimaryKey
    private String path;
    private String extension;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
