package es.atmosferia.arva.arvapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by root on 8/06/16.
 */
public class DBManager {

    private DBHelper dbHelper;

    DBManager(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void insereix(String dev_id, float latitude, float longitude, Date date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.RegisteredDevices.COLUMN_DEV_ID, dev_id);
        values.put(DBContract.RegisteredDevices.COLUMN_LAT, latitude);
        values.put(DBContract.RegisteredDevices.COLUMN_LON, longitude);
        values.put(DBContract.RegisteredDevices.COLUMN_DATE, date.toString());

        db.insert(DBContract.RegisteredDevices.TABLE_NAME, null, values);
    }

    private void eliminar(String dev_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBContract.RegisteredDevices.TABLE_NAME, DBContract.RegisteredDevices.COLUMN_DEV_ID + "=?", new String[] {dev_id});

        db.close();
    }
}
