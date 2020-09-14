package com.cofitconsulting.cofit.user.bilancio.debiti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cofitconsulting.cofit.utility.strutture.StrutturaConto;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static String DATABASE_NAME = "debiti_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TIPO_DEBITO = "tipo_debiti";
    private static final String TABLE_DESCRIZIONE_DEBITO = "descrizione_debiti";
    private static final String TABLE_IMPORTO_DEBITO = "importo_debiti";
    private static final String TABLE_DATA_DEBITO = "data_debiti";
    private static final String TABLE_PAGATO = "credito_pagato";
    private static final String KEY_ID = "id";
    private static final String KEY_TIPO = "tipo";
    private static final String KEY_DESCRIZIONE = "descrizione";
    private static final String KEY_IMPORTO = "importo";
    private static final String KEY_DATA = "data";
    private static final String KEY_PAGATO = "pagato";

    //creo la tabella dei debiti
    private static final String CREATE_TABLE_TIPO_DEBITO = "CREATE TABLE "
            + TABLE_TIPO_DEBITO + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TIPO + " TEXT );";

    private static final String CREATE_TABLE_DESCRIZIONE_DEBITO = "CREATE TABLE "
            + TABLE_DESCRIZIONE_DEBITO + "(" + KEY_ID + " INTEGER,"+ KEY_DESCRIZIONE + " TEXT );";

    private static final String CREATE_TABLE_IMPORTO_DEBITO = "CREATE TABLE "
            + TABLE_IMPORTO_DEBITO + "(" + KEY_ID + " INTEGER,"+ KEY_IMPORTO + " TEXT );";

    private static final String CREATE_TABLE_DATA_DEBITO = "CREATE TABLE "
            + TABLE_DATA_DEBITO + "(" + KEY_ID + " INTEGER,"+ KEY_DATA + " TEXT );";

    private static final String CREATE_TABLE_PAGATO = "CREATE TABLE "
            + TABLE_PAGATO + "(" + KEY_ID + " INTEGER,"+ KEY_PAGATO + " TEXT );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("table", CREATE_TABLE_TIPO_DEBITO);
    }

    //creo il database dei debiti
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIPO_DEBITO);
        db.execSQL(CREATE_TABLE_DESCRIZIONE_DEBITO);
        db.execSQL(CREATE_TABLE_IMPORTO_DEBITO);
        db.execSQL(CREATE_TABLE_DATA_DEBITO);
        db.execSQL(CREATE_TABLE_PAGATO);
    }

    //se il database esiste già e intendo modificarlo
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_TIPO_DEBITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_DESCRIZIONE_DEBITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_IMPORTO_DEBITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_DATA_DEBITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_PAGATO + "'");
        onCreate(db);
    }

    //metodo per aggiungere il debito nel database
    public void addDebito(String tipo, String descrizione, String importo, String data, String pagato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesTipo = new ContentValues();
        valuesTipo.put(KEY_TIPO, tipo);

        long id = db.insertWithOnConflict(TABLE_TIPO_DEBITO, null, valuesTipo, SQLiteDatabase.CONFLICT_IGNORE);

        ContentValues valuesDescrizione = new ContentValues();
        valuesDescrizione.put(KEY_ID, id);
        valuesDescrizione.put(KEY_DESCRIZIONE, descrizione);
        db.insert(TABLE_DESCRIZIONE_DEBITO, null, valuesDescrizione);

        ContentValues valuesImporto = new ContentValues();
        valuesImporto.put(KEY_ID, id);
        valuesImporto.put(KEY_IMPORTO, importo);
        db.insert(TABLE_IMPORTO_DEBITO, null, valuesImporto);

        ContentValues valuesData = new ContentValues();
        valuesData.put(KEY_ID, id);
        valuesData.put(KEY_DATA, data);
        db.insert(TABLE_DATA_DEBITO,null, valuesData);

        ContentValues valuesPagato = new ContentValues();
        valuesPagato.put(KEY_ID, id);
        valuesPagato.put(KEY_PAGATO, pagato);
        db.insert(TABLE_PAGATO,null, valuesPagato);

    }

    //metodo per recuperare i debiti presenti nel database
    public ArrayList<StrutturaConto> getAllDebiti() {
        ArrayList<StrutturaConto> strutturaContoArrayList = new ArrayList<StrutturaConto>();

        String selectQuery = "SELECT  * FROM " + TABLE_TIPO_DEBITO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                StrutturaConto strutturaConto = new StrutturaConto();
                strutturaConto.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                strutturaConto.setTipo(c.getString(c.getColumnIndex(KEY_TIPO)));

                String selectDescrizioneQuery = "SELECT  * FROM " + TABLE_DESCRIZIONE_DEBITO +" WHERE "+KEY_ID+" = "+ strutturaConto.getId();
                Log.d("oppp",selectDescrizioneQuery);

                Cursor cDescrizione = db.rawQuery(selectDescrizioneQuery, null);

                if (cDescrizione.moveToFirst()) {
                    do {
                        strutturaConto.setDescrizione(cDescrizione.getString(cDescrizione.getColumnIndex(KEY_DESCRIZIONE)));
                    } while (cDescrizione.moveToNext());
                }

                String selectImportoQuery = "SELECT  * FROM " + TABLE_IMPORTO_DEBITO+" WHERE "+KEY_ID+" = "+ strutturaConto.getId();;

                Cursor cImporto = db.rawQuery(selectImportoQuery, null);

                if (cImporto.moveToFirst()) {
                    do {
                        strutturaConto.setImporto(cImporto.getString(cImporto.getColumnIndex(KEY_IMPORTO)));
                    } while (cImporto.moveToNext());
                }

                String selectDataQuery = "SELECT  * FROM " + TABLE_DATA_DEBITO+" WHERE "+KEY_ID+" = "+ strutturaConto.getId();;

                Cursor cData = db.rawQuery(selectDataQuery, null);

                if (cData.moveToFirst()) {
                    do {
                        strutturaConto.setData(cData.getString(cData.getColumnIndex(KEY_DATA)));
                    } while (cData.moveToNext());
                }

                String selectPagatoQuery = "SELECT  * FROM " + TABLE_PAGATO+" WHERE "+KEY_ID+" = "+ strutturaConto.getId();;

                Cursor cPagato = db.rawQuery(selectPagatoQuery, null);

                if (cPagato.moveToFirst()) {
                    do {
                        strutturaConto.setPagato(cPagato.getString(cPagato.getColumnIndex(KEY_PAGATO)));
                    } while (cPagato.moveToNext());
                }

                strutturaContoArrayList.add(strutturaConto);
            } while (c.moveToNext());
        }

        return strutturaContoArrayList;
    }

    //metodo per modificare il debito
    public void updateDebito(int id, String tipo, String descrizione, String importo, String data, String pagato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesTipo = new ContentValues();
        valuesTipo.put(KEY_TIPO, tipo);
        db.update(TABLE_TIPO_DEBITO, valuesTipo, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesDescrizione = new ContentValues();
        valuesDescrizione.put(KEY_DESCRIZIONE, descrizione);
        db.update(TABLE_DESCRIZIONE_DEBITO, valuesDescrizione, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesImporto = new ContentValues();
        valuesImporto.put(KEY_IMPORTO, importo);
        db.update(TABLE_IMPORTO_DEBITO, valuesImporto, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesData = new ContentValues();
        valuesData.put(KEY_DATA, data);
        db.update(TABLE_DATA_DEBITO, valuesData, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesPagato = new ContentValues();
        valuesPagato.put(KEY_PAGATO, pagato);
        db.update(TABLE_PAGATO, valuesPagato, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    //metodo per cancellare il debito
    public void deleteDebito(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TIPO_DEBITO, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_DESCRIZIONE_DEBITO, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        db.delete(TABLE_IMPORTO_DEBITO, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_DATA_DEBITO, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_PAGATO, KEY_ID + " = ?",new String[]{String.valueOf(id)});
    }

}