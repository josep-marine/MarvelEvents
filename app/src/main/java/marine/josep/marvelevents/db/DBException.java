package marine.josep.marvelevents.db;

public class DBException extends Exception {

    private static final String ERROR_PRIMARY_KEY = "Primary Key Error";
    private static final String ERROR_FOREIGN_KEY = "Foreign Key Error";
    private static final String ERROR_CLAZZS_NOT_FOUND= "The classes to create the database were not found. They must be specified in the DBInterface getInstance";

    private  DBError error;

    public DBException(DBException. DBError error) {
        super(error.getCause());
        this.error = error;
    }

    public String getInfo(){
        return error.getCause();
    }

    public enum DBError {

        FOREIGN_KEY(ERROR_FOREIGN_KEY),
        PRIMARY_KEY(ERROR_PRIMARY_KEY),
        CLAZZS_NOT_FOUND(ERROR_CLAZZS_NOT_FOUND);

        private String cause;

         DBError(String cause){
            this.cause=cause;
        }

        public String getCause() {
            return cause;
        }
    }
}
