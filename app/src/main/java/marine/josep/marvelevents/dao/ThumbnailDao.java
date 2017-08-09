package marine.josep.marvelevents.dao;

import android.content.Context;

import marine.josep.marvelevents.model.Thumbnail;

public class ThumbnailDao extends GenericSQLDao<Thumbnail> {

    private static ThumbnailDao instance;

    private ThumbnailDao(Context context) {
        super(context,Thumbnail.class);
    }

    public static ThumbnailDao getInstance(Context context){
        if(instance==null){
            instance = new ThumbnailDao(context);
        }
        return  instance;
    }

}