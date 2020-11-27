package com.cofitconsulting.cofit.user.registro_finanziario.crediti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cofitconsulting.cofit.utility.model.ModelRegistro;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "crediti_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TIPO_CREDITO = "tipo_credito";
    private static final String TABLE_DESCRIZIONE_CREDITO = "descrizione_crediti";
    private static final String TABLE_IMPORTO_CREDITO = "importo_crediti";
    private static final String TABLE_DATA_CREDITO = "data_crediti";
    private static final String TABLE_PAGATO = "credito_pagato";
    private static final String KEY_ID = "id";
    private static final String KEY_TIPO = "tipo";
    private static final String KEY_DESCRIZIONE = "descrizione";
    private static final String KEY_IMPORTO = "importo";
    private static final String KEY_DATA = "data";
    private static final String KEY_PAGATO = "pagato";


    //creo la tabella del database per i crediti
    private static final String CREATE_TABLE_TIPO_CREDITO = "CREATE TABLE "
            + TABLE_TIPO_CREDITO + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TIPO + " TEXT );";

    private static final String CREATE_TABLE_DESCRIZIONE_CREDITO = "CREATE TABLE "
            + TABLE_DESCRIZIONE_CREDITO + "(" + KEY_ID + " INTEGER,"+ KEY_DESCRIZIONE + " TEXT );";

    private static final String CREATE_TABLE_IMPORTO_CREDITO = "CREATE TABLE "
            + TABLE_IMPORTO_CREDITO + "(" + KEY_ID + " INTEGER,"+ KEY_IMPORTO + " TEXT );";

    private static final String CREATE_TABLE_DATA_CREDITO = "CREATE TABLE "
            + TABLE_DATA_CREDITO + "(" + KEY_ID + " INTEGER,"+ KEY_DATA + " TEXT );";

    private static final String CREATE_TABLE_PAGATO = "CREATE TABLE "
            + TABLE_PAGATO + "(" + KEY_ID + " INTEGER,"+ KEY_PAGATO + " TEXT );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("table", CREATE_TABLE_TIPO_CREDITO);
    }

    //credo il database dei crediti
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIPO_CREDITO);
        db.execSQL(CREATE_TABLE_DESCRIZIONE_CREDITO);
        db.execSQL(CREATE_TABLE_IMPORTO_CREDITO);
        db.execSQL(CREATE_TABLE_DATA_CREDITO);
        db.execSQL(CREATE_TABLE_PAGATO);
    }

    //se il database esiste già e intendo modificarlo
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_TIPO_CREDITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_DESCRIZIONE_CREDITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_IMPORTO_CREDITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_DATA_CREDITO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_PAGATO + "'");
        onCreate(db);
    }

    public void addCredito(String tipo, String descrizione, String importo, String data, String pagato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesTipo = new ContentValues();
        valuesTipo.put(KEY_TIPO, tipo);

        long id = db.insertWithOnConflict(TABLE_TIPO_CREDITO, null, valuesTipo, SQLiteDatabase.CONFLICT_IGNORE);

        ContentValues valuesDescrizione = new ContentValues();
        valuesDescrizione.put(KEY_ID, id);
        valuesDescrizione.put(KEY_DESCRIZIONE, descrizione);
        db.insert(TABLE_DESCRIZIONE_CREDITO, null, valuesDescrizione);


        ContentValues valuesImporto = new ContentValues();
        valuesImporto.put(KEY_ID, id);
        valuesImporto.put(KEY_IMPORTO, importo);
        db.insert(TABLE_IMPORTO_CREDITO, null, valuesImporto);

        ContentValues valuesData = new ContentValues();
        valuesData.put(KEY_ID, id);
        valuesData.put(KEY_DATA, data);
        db.insert(TABLE_DATA_CREDITO,null, valuesData);

        ContentValues valuesPagato = new ContentValues();
        valuesPagato.put(KEY_ID, id);
        valuesPagato.put(KEY_PAGATO, pagato);
        db.insert(TABLE_PAGATO,null, valuesPagato);

    }

    //metodo per recuperare tutti i crediti salvati nel database
    public ArrayList<ModelRegistro> getAllCrediti() {
        ArrayList<ModelRegistro> modelRegistroArrayList = new ArrayList<ModelRegistro>();

        String selectQuery = "SELECT  * FROM " + TABLE_TIPO_CREDITO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                ModelRegistro modelRegistro = new ModelRegistro();
                modelRegistro.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                modelRegistro.setTipo(c.getString(c.getColumnIndex(KEY_TIPO)));


                String selectDescrizioneQuery = "SELECT  * FROM " + TABLE_DESCRIZIONE_CREDITO +" WHERE "+KEY_ID+" = "+ modelRegistro.getId();
                Log.d("oppp",selectDescrizioneQuery);

                Cursor cDescrizione = db.rawQuery(selectDescrizioneQuery, null);

                if (cDescrizione.moveToFirst()) {
                    do {
                        modelRegistro.setDescrizione(cDescrizione.getString(cDescrizione.getColumnIndex(KEY_DESCRIZIONE)));
                    } while (cDescrizione.moveToNext());
                }


                String selectImportoQuery = "SELECT  * FROM " + TABLE_IMPORTO_CREDITO+" WHERE "+KEY_ID+" = "+ modelRegistro.getId();;

                Cursor cImporto = db.rawQuery(selectImportoQuery, null);

                if (cImporto.moveToFirst()) {
                    do {
                        modelRegistro.setImporto(cImporto.getString(cImporto.getColumnIndex(KEY_IMPORTO)));
                    } while (cImporto.moveToNext());
                }

                String selectDataQuery = "SELECT  * FROM " + TABLE_DATA_CREDITO+" WHERE "+KEY_ID+" = "+ modelRegistro.getId();;

                Cursor cData = db.rawQuery(selectDataQuery, null);

                if (cData.moveToFirst()) {
                    do {
                        modelRegistro.setData(cData.getString(cData.getColumnIndex(KEY_DATA)));
                    } while (cData.moveToNext());
                }

                String selectPagatoQuery = "SELECT  * FROM " + TABLE_PAGATO+" WHERE "+KEY_ID+" = "+ modelRegistro.getId();;

                Cursor cPagato = db.rawQuery(selectPagatoQuery, null);

                if (cPagato.moveToFirst()) {
                    do {
                        modelRegistro.setPagato(cPagato.getString(cPagato.getColumnIndex(KEY_PAGATO)));
                    } while (cPagato.moveToNext());
                }

                modelRegistroArrayList.add(modelRegistro);
            } while (c.moveToNext());
        }
        return modelRegistroArrayList;
    }

    //metodo per modificare il credito
    public void updateCredito(int id, String tipo, String descrizione, String importo, String data, String pagato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesTipo = new ContentValues();
        valuesTipo.put(KEY_TIPO, tipo);
        db.update(TABLE_TIPO_CREDITO, valuesTipo, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesDescrizione = new ContentValues();
        valuesDescrizione.put(KEY_DESCRIZIONE, descrizione);
        db.update(TABLE_DESCRIZIONE_CREDITO, valuesDescrizione, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesImporto = new ContentValues();
        valuesImporto.put(KEY_IMPORTO, importo);
        db.update(TABLE_IMPORTO_CREDITO, valuesImporto, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesData = new ContentValues();
        valuesData.put(KEY_DATA, data);
        db.update(TABLE_DATA_CREDITO, valuesData, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesPagato = new ContentValues();
        valuesPagato.put(KEY_PAGATO, pagato);
        db.update(TABLE_PAGATO, valuesPagato, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    //metodo per cancellare il credito mediante l'id
    public void deleteCredito(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TIPO_CREDITO, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_DESCRIZIONE_CREDITO, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        db.delete(TABLE_IMPORTO_CREDITO, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_DATA_CREDITO, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        db.delete(TABLE_PAGATO, KEY_ID + " = ?",new String[]{String.valueOf(id)});


    }

}