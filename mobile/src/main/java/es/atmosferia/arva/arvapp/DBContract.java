package es.atmosferia.arva.arvapp;
import android.provider.BaseColumns;

public class DBContract {
    // Inicialització de les variables comunes que s'utilitzaran per la creació de les taules
    public static final  int    DATABASE_VERSION    = 1;                 // Versió de la bd
    public static final  String DATABASE_NAME       = "Arvapp.db";       // Nom de la bd
    private static final String TEXT_TYPE           = " TEXT";           // Tipus de text
    private static final String REAL_TYPE           = " REAL";           // Tipus float
    private static final String COMMA_SEP           = ",";               // Separació

    /**
     * Constructora. Se sol definir així per tal que la crida de la constructora de la classe
     * Contract no tingui cap efecte.
     */
    private DBContract() {}

    /**
     * Classe RegisteredDevices (registeredDevices).
     */

    public static abstract class RegisteredDevices implements BaseColumns {
        public static final String TABLE_NAME = "registeredDevices";
        public static final String COLUMN_DEV_ID = "dev_id";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LON = "longitude";
        public static final String COLUMN_LASTUPDATED = "date";

        // String que proporciona la creació de la taula "Table1"
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_DEV_ID + TEXT_TYPE + " UNIQUE" + COMMA_SEP +
                COLUMN_LAT + REAL_TYPE + COMMA_SEP + COLUMN_LON +
                REAL_TYPE + COMMA_SEP + COLUMN_LASTUPDATED + TEXT_TYPE + " )";

        // String que proporciona l'eliminació de la taula "Table1"
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
