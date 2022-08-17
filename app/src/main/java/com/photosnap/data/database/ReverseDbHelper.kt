package com.photosnap.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.photosnap.data.database.ReverseImageContract.Companion.DATABASE_NAME
import com.photosnap.data.database.ReverseImageContract.Companion.DATABASE_TABLE_REVERSEIMAGES
import com.photosnap.data.database.ReverseImageContract.Companion.DATABASE_VERSION
import com.photosnap.data.database.ReverseImageContract.Companion.QUERY_CREATE_REVERSEIMAGES
import com.photosnap.data.database.ReverseImageContract.Companion.QUERY_UPDATE_REVERSEIMAGES
import com.photosnap.data.database.ReverseImageContract.Companion.ROW_DOWNLOADEDIMAGE
import com.photosnap.data.database.ReverseImageContract.Companion.ROW_DOWNLOADEDIMAGENAME
import com.photosnap.data.database.ReverseImageContract.Companion.ROW_DOWNLOADEDIMAGEURL
import com.photosnap.data.database.ReverseImageContract.Companion.ROW_ID

class ReverseDbHelper(ctx: Context) : SQLiteOpenHelper(ctx,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private lateinit var INSTANCE: ReverseDbHelper
        private lateinit var database: SQLiteDatabase
        private var databaseOpen: Boolean = false

        fun closeDatabase() {
            if (database.isOpen && databaseOpen) {
                database.close()
                databaseOpen = false
            }
        }

        fun initDatabaseInstance(ctx: Context): ReverseDbHelper {
            INSTANCE = ReverseDbHelper(ctx)
            return INSTANCE
        }

        fun insertReverseImage(obj:DatabaseModel): Long {

            if (!databaseOpen) {
                database = INSTANCE.writableDatabase
                databaseOpen = true
            }

            val values = ContentValues()
            values.put(ROW_DOWNLOADEDIMAGE, obj.downloadedImage)
            values.put(ROW_DOWNLOADEDIMAGEURL, obj.downloadedImageUrl)
            values.put(ROW_DOWNLOADEDIMAGENAME, obj.downloadedImageName)
            return database.insert(DATABASE_TABLE_REVERSEIMAGES, null, values)
        }

        fun getAllImages(): MutableList<DatabaseModel> {
            if (!databaseOpen) {
                database = INSTANCE.writableDatabase
                databaseOpen = true
            }

            val data: MutableList<DatabaseModel> = ArrayList()
            val cursor = database.rawQuery("SELECT * FROM ${DATABASE_TABLE_REVERSEIMAGES}", null)
            cursor.use { cur ->
                if (cursor.moveToFirst()) {
                    do {
                        val image = DatabaseModel(
                            cur.getInt(0),
                            cur.getBlob(1),
                            cur.getString(2),
                            cur.getString(3)
                        )
                        data.add(image)

                    } while (cursor.moveToNext())
                }
            }
            return data
        }

        fun deleteReverseImageData(id: Int): Int {
            if (!databaseOpen) {
                database = INSTANCE.writableDatabase
                databaseOpen = true
            }
            return database.delete(DATABASE_TABLE_REVERSEIMAGES, "${ROW_ID} = $id", null)
        }

    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(QUERY_CREATE_REVERSEIMAGES)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(QUERY_UPDATE_REVERSEIMAGES)
    }

}