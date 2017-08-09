package marine.josep.marvelevents.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import marine.josep.marvelevents.BuildConfig;
import marine.josep.marvelevents.annotation.SQLForeignKey;
import marine.josep.marvelevents.annotation.SQLPrimaryKey;
import marine.josep.marvelevents.annotation.SQLTransient;
import marine.josep.marvelevents.model.Event;
import marine.josep.marvelevents.model.Thumbnail;
import marine.josep.marvelevents.util.Constants;

public class DBInterface {

    private final static String modelPackage = BuildConfig.APPLICATION_ID + ".model";
    private static DBInterface instance;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    private DBInterface(Context context, List<Class> clazzs, Integer dbVersion) {
        this.dbHelper = new DBHelper(context, clazzs, dbVersion);
    }

    public static DBInterface getInstance(Context context) {

        if (instance == null) {
            List<Class> clazzs = new ArrayList<>();
            clazzs.add(Thumbnail.class);
            clazzs.add(Event.class);
            instance = new DBInterface(context, clazzs, Constants.DB_VERSION);
        }
        return instance;
    }

    public synchronized void truncateTable(Class clazz) {
        open();
        sqLiteDatabase.execSQL("DELETE FROM " + clazz.getSimpleName() + ";");
        sqLiteDatabase.execSQL("VACUUM;");
        close();
    }

    public synchronized Object insertObject(Object object) {

        open();
        Object id;
        ContentValues initialValues = new ContentValues();
        Field[] fields = object.getClass().getDeclaredFields();

        try {

            for (Field field : fields) {

                if (field.getAnnotation(SQLTransient.class) != null) {
                    continue;
                }

                String type = field.getType().getSimpleName();
                field.setAccessible(true);

                if (field.getAnnotation(SQLPrimaryKey.class) == null) {
                    if (type.equals("String")) {
                        initialValues.put(field.getName(), (String) field.get(object));
                    } else if (type.equals("Integer") || type.equals("int")) {
                        initialValues.put(field.getName(), (Integer) field.get(object));
                    } else if (type.equals("Float") || type.equals("float")) {
                        initialValues.put(field.getName(), (Float) field.get(object));
                    } else if (type.equals("Double") || type.equals("double")) {
                        initialValues.put(field.getName(), (Double) field.get(object));
                    } else if (type.equals("Date")) {
                        initialValues.put(field.getName(), (Integer) field.get(object));
                    } else if (type.equals("Boolean") || type.equals("boolean")) {
                        initialValues.put(field.getName(), (Boolean) field.get(object) == true ? 1 : 0);
                    } else if (type.equals("Bitmap") || type.equals("bitmap")) {
                        initialValues.put(field.getName(), bitmapToByteArray((Bitmap) field.get(object)));
                    } else if (field.getAnnotation(SQLForeignKey.class) != null) {
                        Object objectIter = field.get(object);
                        Field[] fieldsIter = objectIter.getClass().getDeclaredFields();
                        for (Field fieldIter : fieldsIter) {
                            fieldIter.setAccessible(true);
                            if (fieldIter.getAnnotation(SQLPrimaryKey.class) != null) {
                                if (fieldIter.get(objectIter) instanceof Integer) {
                                    initialValues.put(field.getName(), (Integer) fieldIter.get(objectIter));
                                } else {
                                    initialValues.put(field.getName(), (String) fieldIter.get(objectIter));
                                }
                                break;
                            }
                        }
                    }
                }else if(field.get(object)!=null){
                    if (type.equals("String")) {
                        initialValues.put(field.getName(), (String) field.get(object));
                    } else if (type.equals("Integer") || type.equals("int")) {
                        initialValues.put(field.getName(), (Integer) field.get(object));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(Constants.PROJECT, e.getMessage());
        }

        id = sqLiteDatabase.insert(object.getClass().getSimpleName(), null, initialValues);
        close();

        return id;
    }

    public synchronized void updateObject(Object object) {

        open();
        ContentValues contentValues = new ContentValues();
        Field[] fields = object.getClass().getDeclaredFields();
        Object id = null;

        try {

            for (Field field : fields) {

                if (field.getAnnotation(SQLTransient.class) != null) {
                    continue;
                }

                String type = field.getType().getSimpleName();

                field.setAccessible(true);

                if (field.getAnnotation(SQLPrimaryKey.class) != null) {
                    id = field.get(object);
                } else {

                    if (type.equals("String")) {
                        contentValues.put(field.getName(), (String) field.get(object));
                    } else if (type.equals("Integer") || type.equals("int")) {
                        contentValues.put(field.getName(), (Integer) field.get(object));
                    } else if (type.equals("Float") || type.equals("float")) {
                        contentValues.put(field.getName(), (Float) field.get(object));
                    } else if (type.equals("Double") || type.equals("double")) {
                        contentValues.put(field.getName(), (Double) field.get(object));
                    } else if (type.equals("Date")) {
                        contentValues.put(field.getName(), (Integer) field.get(object));
                    } else if (type.equals("Bitmap") || type.equals("bitmap")) {
                        contentValues.put(field.getName(), bitmapToByteArray((Bitmap) field.get(object)));
                    } else if (type.equals("Boolean") || type.equals("boolean")) {
                        contentValues.put(field.getName(), (Boolean) field.get(object) == true ? 1 : 0);
                    } else if (field.getAnnotation(SQLForeignKey.class) != null) {
                        Object objectIter = field.get(object);
                        Field[] fieldsIter = objectIter.getClass().getDeclaredFields();
                        for (Field fieldIter : fieldsIter) {
                            fieldIter.setAccessible(true);
                            if (fieldIter.getAnnotation(SQLPrimaryKey.class) != null) {
                                if (fieldIter.get(objectIter) instanceof Integer) {
                                    contentValues.put(field.getName(), (Integer) fieldIter.get(objectIter));
                                } else {
                                    contentValues.put(field.getName(), (String) fieldIter.get(objectIter));
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.e(Constants.PROJECT, e.getMessage());
        }

        try {
            sqLiteDatabase.update(object.getClass().getSimpleName(), contentValues, dbHelper.getNameId(object.getClass().getSimpleName()) + "=\"" + id + "\"", null);
        } catch (DBException e) {
            Log.e(Constants.PROJECT, e.getInfo());
        }

        close();

    }

    public Object getEntity(Class clazz, Object id) {
        try {
            String where = dbHelper.getNameId(clazz.getSimpleName()) + "=\"" + id + "\"";
            List<?> objects = getAll(clazz, where);
            if (objects != null && objects.size() > 0) {
                return objects.get(0);
            }
        } catch (DBException e) {
            Log.e(Constants.PROJECT, e.getInfo());
        }
        return null;
    }

    public List<?> getAll(Class clazz) {
        return getAll(clazz, null);
    }

    public List<?> getAll(Class clazz, String where) {

        open();
        List<Object> list = new ArrayList<>();
        List<String> tableColumns = new ArrayList<>();
        Field[] fieldsColumns = clazz.getDeclaredFields();

        for (Field field : fieldsColumns) {

            if (field.getAnnotation(SQLTransient.class) != null) {
                continue;
            }

            if (!field.getType().getSimpleName().equals("List")) {
                tableColumns.add(field.getName());
            }
        }

        try {

            Cursor cursor = sqLiteDatabase.query(clazz.getSimpleName(), tableColumns.toArray(new String[tableColumns.size()]), where, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {

                    Object object = clazz.newInstance();

                    for (String nomColumna : tableColumns) {
                        Field field = clazz.getDeclaredField(nomColumna);
                        field.setAccessible(true);

                        String type = field.getType().getSimpleName();

                        field.setAccessible(true);

                        if (field.getAnnotation(SQLTransient.class) != null) {
                            continue;
                        }

                        if (type.equals("String")) {
                            field.set(object, cursor.getString(cursor.getColumnIndex(nomColumna)));
                        } else if (type.equals("Integer") || type.equals("int")) {
                            field.set(object, cursor.getInt(cursor.getColumnIndex(nomColumna)));
                        } else if (type.equals("Float") || type.equals("float")) {
                            field.set(object, cursor.getFloat(cursor.getColumnIndex(nomColumna)));
                        } else if (type.equals("Double") || type.equals("double")) {
                            field.set(object, cursor.getDouble(cursor.getColumnIndex(nomColumna)));
                        } else if (type.equals("Date")) {
                            field.set(object, cursor.getInt(cursor.getColumnIndex(nomColumna)));
                        } else if (type.equals("Boolean") || type.equals("boolean")) {
                            field.set(object, cursor.getInt(cursor.getColumnIndex(nomColumna)) > 0);
                        } else if (type.equals("Bitmap") || type.equals("bitmap") ) {
                            field.set(object, byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(nomColumna))));
                        } else if (type.equals("List")) {
                            //TODO
                        } else if (field.getAnnotation(SQLForeignKey.class) != null) {
                            Class iterClazz = Class.forName(modelPackage + "." + field.getAnnotation(SQLForeignKey.class).table());
                            Object id = null;

                            SQLForeignKey.IdType idType = field.getAnnotation(SQLForeignKey.class).idType();
                            switch (idType) {
                                case INTEGER:
                                    id = cursor.getInt(cursor.getColumnIndex(nomColumna));
                                    break;
                                case STRING:
                                    id = cursor.getString(cursor.getColumnIndex(nomColumna));
                                    break;
                            }

                            Object iterObject = getEntity(iterClazz, id);
                            field.set(object, iterObject);
                        }
                    }
                    list.add(object);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e(Constants.PROJECT, e.getMessage());
        }

        close();

        return list;
    }

    public void deleteObject(Class clazz, Object id) {
        open();
        String sql = "DELETE FROM " + clazz.getSimpleName() + " WHERE id=\"" + id + "\"";
        sqLiteDatabase.execSQL(sql);
        close();
    }

    public DBInterface open() {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private static class DBHelper extends SQLiteOpenHelper {

        private List<Class> clazzs;

        DBHelper(Context context, List<Class> clazzs, Integer dbVersio) {
            super(context, context.getPackageName(), null, dbVersio);
            this.clazzs = clazzs;
            getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                if (clazzs != null && clazzs.size() > 0) {
                    for (Class clazz : clazzs) {
                        db.execSQL(getSQLcreateBD(clazz));
                    }
                } else {
                    throw new DBException(DBException.DBError.CLAZZS_NOT_FOUND);
                }
            } catch (DBException e) {
                Log.e(Constants.PROJECT, e.getInfo());
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int VersioNova) {
            //TODO
        }

        private String getSQLcreateBD(Class clazz) throws DBException {

            String sql = "";

            if (clazz != null) {

                sql = sql + " create table " + clazz.getSimpleName() + " (";
                String foreignKeys = "";

                Field[] fields = clazz.getDeclaredFields();

                for (Field field : fields) {

                    String type = field.getType().getSimpleName();

                    if (field.getAnnotation(SQLPrimaryKey.class) != null) {
                        if (type.equals("Integer") || type.equals("int")) {
                            sql = sql + field.getName() + " INTEGER PRIMARY KEY,";
                        } else if (type.equals("String")) {
                            sql = sql + field.getName() + " TEXT PRIMARY KEY,";
                        } else {
                            throw new DBException(DBException.DBError.PRIMARY_KEY);
                        }

                    } else if (field.getAnnotation(SQLForeignKey.class) != null) {
                        String foreignTable = field.getAnnotation(SQLForeignKey.class).table();
                        SQLForeignKey.IdType idType = field.getAnnotation(SQLForeignKey.class).idType();
                        switch (idType) {
                            case INTEGER:
                                sql = sql + field.getName() + " INTEGER,";
                                break;
                            case STRING:
                                sql = sql + field.getName() + " TEXT,";
                                break;
                        }
                        foreignKeys = foreignKeys + " FOREIGN KEY(" + field.getName() + ") REFERENCES " + foreignTable + "(" + getNameId(foreignTable) + "),";
                    } else if (field.getAnnotation(SQLTransient.class) != null) {
                        //Nothing
                    } else {
                        if (type.equals("String")) {
                            sql = sql + field.getName() + " TEXT,";
                        } else if (type.equals("Integer") || type.equals("int")) {
                            sql = sql + field.getName() + " INTEGER,";
                        } else if (type.equals("Float") || type.equals("float")) {
                            sql = sql + field.getName() + " REAL,";
                        } else if (type.equals("Double") || type.equals("double")) {
                            sql = sql + field.getName() + " REAL,";
                        } else if (type.equals("Boolean") || type.equals("boolean")) {
                            sql = sql + field.getName() + " INTEGER,";
                        } else if (type.equals("Date")) {
                            sql = sql + field.getName() + " INTEGER,";
                        } else if (type.equals("Bitmap") || type.equals("bitmap") ) {
                            sql = sql + field.getName() + " BLOB,";
                        }
                    }
                }

                if (foreignKeys != null && foreignKeys.length() > 0) {
                    foreignKeys = foreignKeys.substring(0, foreignKeys.length() - 1);
                } else {
                    sql = sql.substring(0, sql.length() - 1);
                }
                sql = sql + foreignKeys;
                sql = sql + ") ";
            }
            return sql;
        }

        private String getNameId(String className) throws DBException {
            if (className != null) {
                try {
                    Class clazz = Class.forName(modelPackage + "." + className);
                    Field[] fields = clazz.getDeclaredFields();

                    for (Field field : fields) {
                        if (field.getAnnotation(SQLPrimaryKey.class) != null) {
                            return field.getName();
                        }
                    }
                    throw new DBException(DBException.DBError.FOREIGN_KEY);
                } catch (ClassNotFoundException e) {
                    throw new DBException(DBException.DBError.FOREIGN_KEY);
                }
            }
            return null;
        }
    }

    private Bitmap byteArrayToBitmap(byte[] byteArray){
        if(byteArray!=null){
          return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        return null;
    }

    private byte[] bitmapToByteArray(Bitmap bitmap){
        if(bitmap!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return  byteArray;
        }
       return null;
    }

}
