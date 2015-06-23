package edu.jcu.emustee15.nevermissing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationsDataSource
{
	// This the class to handle DAO.It maintains the database connection and
	// supports adding new comments and fetching all comments. This was mostly
	// taken from in class. 
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_KEY, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_DESCRIPTION, MySQLiteHelper.COLUMN_IMAGE_PATH,
			MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE, MySQLiteHelper.COLUMN_TYPE, MySQLiteHelper.COLUMN_ASSOCIATE_WITH_KEY };

	
	// Constructor
	public LocationsDataSource(Context context)
	{
		dbHelper = new MySQLiteHelper(context);
	}

	// Open the database for writing
	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	// close the database
	public void close()
	{
		dbHelper.close();
	}

	// Create and return a small object
	public ObjectLocation createSmallObject(String name, String description, String imagePath, long associateWithKey)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
		values.put(MySQLiteHelper.COLUMN_IMAGE_PATH, imagePath);
		values.put(MySQLiteHelper.COLUMN_TYPE, ObjectLocation.TYPE_SMALL_OBJECT);
		values.put(MySQLiteHelper.COLUMN_ASSOCIATE_WITH_KEY, associateWithKey);
		long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_KEY + "=" + insertId, null, null, null, null);
		cursor.moveToFirst();
		ObjectLocation objectLocation = cursorToSmallObjectLocation(cursor);
		cursor.close();

		// This is done so when the user views the small object, they cannot accidently delete the small object within the
		// small object. What they see is actually a copy, the original is only deleteable on the Home screen. 
		if (associateWithKey == -1)
		{
			createSmallObject(objectLocation.getName(), objectLocation.getDescription(), objectLocation.getImagePath(), objectLocation.getId());
		}

		return objectLocation;
	}

	// Create and reutrn a large object
	public ObjectLocation createLargeObject(String name, double latitude, double longitude, String imagePath, long associateWithKey)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
		values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
		values.put(MySQLiteHelper.COLUMN_IMAGE_PATH, imagePath);
		values.put(MySQLiteHelper.COLUMN_TYPE, ObjectLocation.TYPE_LARGE_OBJECT);
		values.put(MySQLiteHelper.COLUMN_ASSOCIATE_WITH_KEY, associateWithKey);
		long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_KEY + "=" + insertId, null, null, null, null);
		cursor.moveToFirst();
		ObjectLocation objectLocation = cursorToLargeObjectLocation(cursor);
		cursor.close();
		return objectLocation;
	}

	// This method takes a cursor and turns it into a small object
	private ObjectLocation cursorToSmallObjectLocation(Cursor cursor)
	{
		ObjectLocation smallLocation = new ObjectLocation(cursor.getString(1), cursor.getLong(0), cursor.getString(2), cursor.getString(3));
		return smallLocation;
	}

	// This method takes a cursor and turns it into a large object
	private ObjectLocation cursorToLargeObjectLocation(Cursor cursor)
	{
		ObjectLocation largeLocation = new ObjectLocation(cursor.getString(1), cursor.getLong(0), cursor.getDouble(4), cursor.getDouble(5), cursor.getString(3));
		return largeLocation;
	}

	// This method deletes an object from the database and attempts to delete
	// all associated files with the entry. 
	public void deleteLocation(ObjectLocation location)
	{
		List<ObjectLocation> locations = getObjectsAssociatedWithId(location.getId());
		long id = location.getId();
		database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_KEY + "=" + id, null);
		database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_ASSOCIATE_WITH_KEY + "=" + id, null);

		for (ObjectLocation loc : locations)
		{
			String fileName = loc.getImagePath();
			File file = new File(fileName);
			file.delete();

		}
	}

	// Get all of the root objects from the database. Subobjects are not returned. 
	public List<ObjectLocation> getAllObjectLocations()
	{
		List<ObjectLocation> myLocations = new ArrayList<ObjectLocation>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_ASSOCIATE_WITH_KEY + "=" + -1, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			if (cursor.getLong(6) == ObjectLocation.TYPE_LARGE_OBJECT)
			{
				myLocations.add(cursorToLargeObjectLocation(cursor));
			}
			else
			{
				myLocations.add(cursorToSmallObjectLocation(cursor));
			}
			cursor.moveToNext();
		}
		cursor.close();
		return myLocations;
	}

	// This method returns all the subobjects associated with an ID. 
	public List<ObjectLocation> getObjectsAssociatedWithId(long ID)
	{
		List<ObjectLocation> myLocations = new ArrayList<ObjectLocation>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_ASSOCIATE_WITH_KEY + "=" + ID, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			if (cursor.getLong(6) == ObjectLocation.TYPE_LARGE_OBJECT)
			{
				myLocations.add(cursorToLargeObjectLocation(cursor));
			}
			else
			{
				myLocations.add(cursorToSmallObjectLocation(cursor));
			}
			cursor.moveToNext();
		}

		for (ObjectLocation location : myLocations)
		{
			location.setNameToDescription();
		}

		cursor.close();
		return myLocations;
	}
}
