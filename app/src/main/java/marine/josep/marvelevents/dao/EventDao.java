package marine.josep.marvelevents.dao;

import android.content.Context;

import marine.josep.marvelevents.model.Event;


public class EventDao extends GenericSQLDao<Event> {

    private static EventDao instance;

    private EventDao(Context context) {
        super(context,Event.class);
    }

    public static EventDao getInstance(Context context){
        if(instance==null){
            instance = new EventDao(context);
        }
        return  instance;
    }

}