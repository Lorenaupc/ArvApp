package es.atmosferia.arva.arvapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DBManager {

    private DBHelper dbHelper;

    DBManager(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void insereix(String dev_id, double latitude, double longitude) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar c = GregorianCalendar.getInstance();
        c.get(Calendar.HOUR_OF_DAY);

        ContentValues values = new ContentValues();
        values.put(DBContract.RegisteredDevices.COLUMN_DEV_ID, dev_id);
        values.put(DBContract.RegisteredDevices.COLUMN_LAT, latitude);
        values.put(DBContract.RegisteredDevices.COLUMN_LON, longitude);
        values.put(DBContract.RegisteredDevices.COLUMN_LASTUPDATED, c.toString());

        db.insert(DBContract.RegisteredDevices.TABLE_NAME, null, values);
    }

    public void insereix(DVA dva){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.RegisteredDevices.COLUMN_DEV_ID, dva.getDev_id());
        values.put(DBContract.RegisteredDevices.COLUMN_LAT, dva.getLatitude());
        values.put(DBContract.RegisteredDevices.COLUMN_LON, dva.getLongitude());
        values.put(DBContract.RegisteredDevices.COLUMN_LASTUPDATED, dva.getLastUpdate());

        db.insert(DBContract.RegisteredDevices.TABLE_NAME, null, values);
    }

    private void eliminar(String dev_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBContract.RegisteredDevices.TABLE_NAME, DBContract.RegisteredDevices.COLUMN_DEV_ID + "=?", new String[] {dev_id});

        db.close();
    }

    public ArrayList<DVA> query(String devId_searched){
        ArrayList<DVA> dvaArr = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DBContract.RegisteredDevices.TABLE_NAME +
                " WHERE " + DBContract.RegisteredDevices.COLUMN_DEV_ID + " ='" +
                devId_searched + "'";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                DVA dva = new DVA();
                dva.setDev_id(cursor.getString(cursor.getColumnIndex(DBContract.RegisteredDevices.COLUMN_DEV_ID)));;
                dva.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBContract.RegisteredDevices.COLUMN_LAT)));;
                dva.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBContract.RegisteredDevices.COLUMN_LON)));;
                dva.setLastUpdate(cursor.getString(cursor.getColumnIndex(DBContract.RegisteredDevices.COLUMN_LASTUPDATED)));;
                dvaArr.add(dva);
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return dvaArr;
    }

}
