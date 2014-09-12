package com.zjut.navigationdrawerdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PersonService {
    private DataBaseOpenHelper dbOpenHelper;

    public PersonService(Context context) {
        //dbOpenHelper=new DataBaseOpenHelper(context);
        dbOpenHelper = DataBaseOpenHelper.getInstance(context);
    }

    public void save(Person person) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", person.getId());
        contentValues.put("name", person.getName());
        contentValues.put("age", person.getAge());
        database.insert("Person", null, contentValues);

        database.close();

    }

    public void update(Person person) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", person.getName());
        contentValues.put("age", person.getAge());
        database.update("Person", contentValues, "id=?",
                new String[]{String.valueOf(person.getId())});
    }

    public void update2(Person person) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        database.execSQL(
                "update Person set name=?,age=? where id=?",
                new Object[]{person.getName(), person.getAge(),
                        person.getId()});
        database.close();
    }

    //按照name来查找person
    /*public Person find(String name)
    {
        SQLiteDatabase database=dbOpenHelper.getWritableDatabase();
        Cursor cursor = database.query("person", new String[] { "personid", "name", "age" }, "name=?",
                new String[] { String.valueOf(name) }, null, null, null);
        if(cursor.moveToNext())
        {
            return new Person(cursor.getInt(0), name, cursor.getInt(2));
        }
        return null;
    }*/
    //按照id来查找person
    public Person find(int id) {
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("Person", new String[]{"id", "name", "age"}, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            return new Person(cursor.getInt(0), cursor.getString(1), cursor.getShort(2));
        }
        return null;
    }

    public long getCount() {
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("Person", new String[]{"count(*)"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    public long getMaxID() {
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("Person", new String[]{"max(id)"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0) + 1;
        }

        return 0;
    }

    public List<Person> getSScrollData(int startResult, int maxResult) {
        List<Person> persons = new ArrayList<Person>();
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("Person", new String[]{"id", "name", "age"}, null, null, null, null, "id asc", startResult + "," + maxResult);

        while (cursor.moveToNext()) {
            persons.add(new Person(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
        }
        cursor.close();
        database.close();

        return persons;
    }

    public void delete(int id) {
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        database.delete("person", "id =?", new String[]{String.valueOf(id)});
        database.close();

    }

    public void deleteDatabase(Context context) {
        dbOpenHelper.deleteDatabase(context);
        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
    }
}
