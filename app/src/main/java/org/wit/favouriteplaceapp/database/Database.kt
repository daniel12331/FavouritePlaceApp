package org.wit.favouriteplaceapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import org.wit.favouriteplaceapp.models.PlaceModel

//https://youtu.be/CzGNaiSoh7E
//creating the database logic, extending the SQLiteOpenHelper base class

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FavourtiePlaceDatabase"
        private const val TABLE_DATA = "FavouritePlaceTable"

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_TABLE = ("CREATE TABLE " + TABLE_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_DATA")
        onCreate(db)
    }


    //Function to insert details to SQLite Database
    fun addFavouritePlace(favPlace: PlaceModel): Long {
        //Writable database so we can write in the database
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, favPlace.title)
        contentValues.put(KEY_IMAGE, favPlace.image)
        contentValues.put(KEY_DESCRIPTION, favPlace.description)
        contentValues.put(KEY_DATE, favPlace.date)
        contentValues.put(KEY_LOCATION, favPlace.location)
        contentValues.put(KEY_LATITUDE, favPlace.latitude)
        contentValues.put(KEY_LONGITUDE, favPlace.longitude)

        // inserting Row
        val result = db.insert(TABLE_DATA, null, contentValues)
        // closing database connection
        db.close()
        return result
    }


    // function to read all the list data which are inserted
    @SuppressLint("Range")
    fun getFavouritePlaceList(): ArrayList<PlaceModel> {

        // A list is initialize using the data model class in which we will add the values from cursor
        val FavPlaceList: ArrayList<PlaceModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_DATA" // Database select query

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = PlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )
                    FavPlaceList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return FavPlaceList
    }
}