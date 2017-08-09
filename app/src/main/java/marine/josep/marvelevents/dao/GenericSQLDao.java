package marine.josep.marvelevents.dao;

import android.content.Context;

import java.util.List;

import marine.josep.marvelevents.db.DBInterface;

public abstract class GenericSQLDao<T>{

    private DBInterface dbInterface;
    private Class clazz;

    public GenericSQLDao(Context context, Class clazz){
        this.clazz = clazz;
        dbInterface = DBInterface.getInstance(context);
    }

    public void create(T object){
        dbInterface.insertObject(object);
    }

    public void update(T object){
         dbInterface.updateObject(object);
    }

    public T find(Integer id){
        return (T) dbInterface.getEntity(clazz, id);
    }

    public List<T> getAll(){
        return (List<T>) dbInterface.getAll(clazz);
    }

    public List<T> getAll(String where){
        return (List<T>) dbInterface.getAll(clazz,where);
    }

    public void remove(Integer id){
        dbInterface.deleteObject(clazz,id);
    }
}
