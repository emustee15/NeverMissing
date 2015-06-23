package edu.jcu.emustee15.nevermissing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	// This class is mostly taken from class, with some modifications to allow for more 
	// things. 
	
	public static final String TABLE_NAME = "locations";
	public static final String COLUMN_KEY = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_IMAGE_PATH = "image";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_ASSOCIATE_WITH_KEY = "associatedWithKey";
	
	private static final String DATABASE_NAME = "locations";
	private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "(" + COLUMN_KEY + 
			" integer primary key autoincrement, " + COLUMN_NAME + " text not null, " +  COLUMN_DESCRIPTION + " text, " + COLUMN_IMAGE_PATH + " text, " + 
			COLUMN_LATITUDE + " real, " + COLUMN_LONGITUDE + " real, "  + COLUMN_TYPE + " integer not null, " + COLUMN_ASSOCIATE_WITH_KEY + " integer not null" +")";

	public static final int DATABASE_VERSION = 7;
	
	// Constructor
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creates database
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.d("Database ", "database created");
		
	}

	
	// Deletes database when upgrading version number. Since this is the first release of the app, there is
	// no need to have anything here. If the app were to recieve future upgrades, here is where the code would
	// go for migrating everything over to a new database safely.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from verion " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
	}

}
