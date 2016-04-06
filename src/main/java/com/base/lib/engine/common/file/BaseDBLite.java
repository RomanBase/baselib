package com.base.lib.engine.common.file;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.base.lib.engine.Base;

import java.io.File;
import java.util.Locale;

/**
 *
 */
public class BaseDBLite extends SQLiteOpenHelper {

    private static final String COMMA = ",";

    private SQLiteDatabase DB;

    private String table;
    private String[] column;

    public BaseDBLite(String name, String table, String... columns) {
        super(Base.appContext, name, null, 1);

        this.table = table;
        column = columns;
    }

    public void openToWrite() {

        if (DB != null) {
            close();
        }

        DB = this.getWritableDatabase();
    }

    public void openToRead() {

        if (DB != null) {
            close();
        }

        DB = this.getReadableDatabase();
    }

    public void close() {

        DB = null;
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public static void createTable(String dbName, String table, String... columnExpresion) {

        BaseDBLite db = new BaseDBLite(dbName, table, columnExpresion);
        db.openToWrite();
        db.createTable();
        db.close();
    }

    public static void addColumn(String dbName, String table, String... columnExpresion) {

        BaseDBLite db = new BaseDBLite(dbName, table);
        db.openToWrite();
        for (String column : columnExpresion) {
            db.addColumn(column);
        }
        db.close();
    }

    protected void createTable() {

        DB.execSQL(String.format(Locale.US, "CREATE table %s (%s)", table, createExpression()));
    }

    public void addColumn(String column) {

        DB.execSQL(String.format(Locale.US, "ALTER table %s ADD %s", table, column));
    }

    private String createExpression() {

        StringBuilder builder = new StringBuilder();

        for (String cex : column) {
            builder.append(cex).append(COMMA);
        }

        builder.setLength(Math.max(builder.length() - 1, 0));
        return builder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP table IF EXISTS " + table);
        onCreate(db);
    }

    public void drop() {

        DB.execSQL("DROP table IF EXISTS " + table);
    }

    public boolean insertRow(String... values) {

        ContentValues content = new ContentValues(values.length);

        for (int i = 0; i < values.length; i++) {
            content.put(column[i], values[i]);
        }

        return DB.insert(table, null, content) > -1;
    }

    public boolean insertIfNotExist(String... values) {

        ContentValues content = new ContentValues(values.length);

        for (int i = 0; i < values.length; i++) {
            content.put(column[i], values[i]);
        }

        return DB.insertWithOnConflict(table, null, content, SQLiteDatabase.CONFLICT_IGNORE) > -1;
    }

    public boolean updateRow(String id, String column, String value) {

        ContentValues values = new ContentValues();
        values.put(column, value);

        return DB.update(table, values, this.column[0] + " = ?", new String[]{id}) > 0;
    }

    public boolean updateRow(String id, String column, String value, String where, String[] whereArgs) {

        ContentValues values = new ContentValues();
        values.put(column, value);

        String[] args = new String[whereArgs.length + 1];
        args[0] = id;
        System.arraycopy(whereArgs, 0, args, 1, whereArgs.length);

        return DB.update(table, values, String.format(Locale.US, "%s = ? AND %s", this.column[0], where), args) > 0;
    }

    public void deleteRows() {

        DB.delete(table, null, null);
    }

    public String[][] getData() {

        Cursor cursor = DB.query(table, column, null, null, null, null, null);

        return getCursorData(cursor);
    }

    public String[][] getData(String... column) {

        if (column == null) {
            column = this.column;
        }

        Cursor cursor = DB.query(table, column, null, null, null, null, null);

        return getCursorData(cursor);
    }

    public String[][] getData(String where, String[] args, String orderBy) {

        Cursor cursor = DB.query(table, column, where, args, null, null, orderBy);

        return getCursorData(cursor);
    }

    public String[][] getData(String select, String sqlCondition, String[] args) {


        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT %s FROM %s %s", select, table, sqlCondition), args);

        return getCursorData(cursor);
    }

    public String[][] getData(String rawSql, String[] args) {

        Cursor cursor = DB.rawQuery(rawSql, args);

        return getCursorData(cursor);
    }

    public String[] getRow(String id) {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT * FROM %s WHERE %s = ?", table, column[0]), new String[]{id});

        String[] row = null;

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            row = getCursorRow(cursor);
        }

        cursor.close();

        return row;
    }

    public String getMin(String column) {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT MIN(%s) FROM %s", column, table), null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            String min = cursor.getString(0);
            cursor.close();
            return min;
        }

        cursor.close();

        return null;
    }

    public String getMax(String column) {

        Cursor cursor = DB.rawQuery(String.format(Locale.US, "SELECT MAX(%s) FROM %s", column, table), null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            String min = cursor.getString(0);
            cursor.close();
            return min;
        }

        cursor.close();

        return null;
    }

    public String[][] getRandom(String where, String[] whereArgs, int limit) {

        return getData(where, whereArgs, "RANDOM() LIMIT " + limit);
    }

    public boolean isEmpty() {

        Cursor cursor = DB.query(table, column, null, null, null, null, null);

        int count = cursor.getCount();
        boolean isEmpty = count == 0;

        cursor.close();

        return isEmpty;
    }

    public int getColumnIndex(String name) {

        for (int i = 0; i < column.length; i++) {
            if (name.equals(column[i])) {
                return i;
            }
        }

        return -1;
    }

    public String getColumnName(int index) {

        return column[index];
    }

    public int getColumnCount() {

        return column.length;
    }

    public String getTable() {

        return table;
    }

    public void setTable(String name) {

        this.table = name;
    }

    public String[] getColumn() {
        return column;
    }

    public void setColumn(String... column) {
        this.column = column;
    }

    public static String[][] getCursorData(Cursor cursor) {

        String[][] data = new String[cursor.getCount()][];
        if (data.length > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                data[cursor.getPosition()] = getCursorRow(cursor);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return data;
    }

    public static String[] getCursorRow(Cursor cursor) {

        int count = cursor.getColumnCount();
        String[] row = new String[count];
        for (int i = 0; i < count; i++) {
            row[i] = cursor.getString(i);
        }

        return row;
    }

    public static boolean exist(String dbName) {
        File dbFile = Base.appContext.getDatabasePath(dbName);

        return dbFile.exists();
    }

    public static void delete(String dbName) {

        Base.appContext.deleteDatabase(dbName);
    }

    public static String varcharKey(String name) {

        return name + " VARCHAR PRIMARY KEY NOT NULL";
    }

    public static String integerKey(String name) {

        return name + " INTEGER PRIMARY KEY NOT NULL";
    }

    public static String varcharNotNull(String name) {

        return name + " VARCHAR NOT NULL";
    }

    public static String integerNotNull(String name) {

        return name + " INTEGER NOT NULL";
    }

    public static String varchar(String name) {

        return name + " VARCHAR";
    }

    public static String integer(String name) {

        return name + " INTEGER";
    }
}
