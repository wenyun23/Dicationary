package com.example.dicationary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

//创建数据库
public class DataBase extends SQLiteOpenHelper {

    public DataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public DataBase(Context context) {
        super(context, "Notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一个叫notes的表，id，word,date三个属性
        db.execSQL("create table Notes(id integer primary key autoincrement," +
                "word text not null," +
                "date text not null)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
