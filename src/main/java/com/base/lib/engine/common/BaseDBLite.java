package com.base.lib.engine.common;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.base.lib.engine.Base;

import java.io.File;

/**
 *
 */
public class BaseDBLite extends SQLiteOpenHelper {

    private static final String COMMA = ",";

    private SQLiteDatabase DB;

    private final String TABLE;
    private final String[] COLUMN_EXP;
    private final String[] COLUMN;

    public BaseDBLite(String name, String table, String... columnExpression) {
        super(Base.context, name, null, 1);

        TABLE = table;
        COLUMN_EXP = columnExpression;
        COLUMN = new String[COLUMN_EXP.length];
        for(int i = 0; i<COLUMN.length; i++){
            COLUMN[i] = COLUMN_EXP[i].split(" ")[0];
        }
    }

    public void openToWrite(){

        if(DB != null){
            close();
        }

        DB = this.getWritableDatabase();
    }

    public void openToRead(){

        if(DB != null){
            close();
        }

        DB = this.getReadableDatabase();
    }

    public void close(){

        DB = null;
        super.close();
    }

    public String getTableName(){

        return TABLE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public void createTable(){

        DB.execSQL("CREATE TABLE " + TABLE + " (" + createExpression() + ")");
    }

    private String createExpression(){

        StringBuilder builder = new StringBuilder();

        for(String cex : COLUMN_EXP){
            builder.append(cex).append(COMMA);
        }

        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void drop(){

        DB.execSQL("DROP TABLE IF EXISTS " + TABLE);
    }

    public void delete(){

        Base.context.deleteDatabase(getDatabaseName());
    }

    public boolean insertRow(String... values){

        ContentValues content = new ContentValues(values.length);

        for(int i = 0; i<values.length; i++){
            content.put(COLUMN[i], values[i]);
        }

        return DB.insert(TABLE, null, content) > -1;
    }

    public boolean updateRow(String id, String column, String value){

        ContentValues values = new ContentValues();
        values.put(column, value);

        return DB.update(TABLE, values, COLUMN[0] + " = ?", new String[]{id}) > 0;
    }

    public boolean updateRow(String id, String column, String value, String columnWhere){

        ContentValues values = new ContentValues();
        values.put(column, value);

        return DB.update(TABLE, values, COLUMN[0] + " = ? AND "+column+" "+columnWhere+" ?", new String[]{id, value}) > 0;
    }

    public void deleteRows(){

        DB.delete(TABLE, null, null);
    }

    public String[][] getData(){

        Cursor cursor = DB.query(TABLE, COLUMN, null, null, null, null, null);

        String[][] data = new String[cursor.getCount()][];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            data[cursor.getPosition()] = row(cursor);
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }

    public String[][] getData(String... column){

        if(column == null){
            column = COLUMN;
        }

        Cursor cursor = DB.query(TABLE, column, null, null, null, null, null);

        String[][] data = new String[cursor.getCount()][];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            data[cursor.getPosition()] = row(cursor);
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }

    public String[] getRow(String id){

        Cursor cursor = DB.rawQuery("SELECT * FROM "+ TABLE + " WHERE "+ COLUMN[0] +" = ?", new String[]{id});

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            String[] row = new String[cursor.getColumnCount()];
            for(int i = 0; i<row.length; i++){
                row[i] = cursor.getString(i);
            }
            cursor.close();
            return row;
        }

        cursor.close();

        return null;
    }

    private String[] row(Cursor cursor){

        String[] row = new String[COLUMN.length];
        int cc = cursor.getColumnCount();
        for(int i = 0; i<cc; i++){
            row[i] = cursor.getString(i);
        }

        return row;
    }

    private String createWhere(String where) {

        return where != null ? " WHERE " + where : "";
    }

    public static boolean exist(String dbName) {
        File dbFile = Base.context.getDatabasePath(dbName);

        return dbFile.exists();
    }

    public static void delete(String dbName){

        Base.context.deleteDatabase(dbName);
    }
}
