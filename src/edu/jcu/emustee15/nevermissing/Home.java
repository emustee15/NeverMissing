package edu.jcu.emustee15.nevermissing;

import java.util.List;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Home extends ListActivity
{
	/*
	 * Author:			Eric Mustee
	 * Date:			5/8/2014
	 * Description:		This android application is called "Never Missing." It allows you to keep track
	 * 					of where you placed various objects, large or small. For small objects, the app
	 * 					asks you to write a description of where you placed the small object, and perhaps
	 * 					take a picture. For large objects, the app gets a locations from the GPS hardware
	 * 					and then can use a compass to navigate back to the object. 
	 */
	
	
	// Various Request codes
	public final static int REQUEST_CODE_CREATE_SMALL = 0;
	public final static int REQUEST_CODE_CREATE_LARGE = 1;
	public final static int REQUEST_CODE_VIEW_SMALL = 2;
	public final static int REQUEST_CODE_COMPASS = 4;
	private FragmentManager fragmentManager;
	private TextView hintText;

	// Our data source
	private LocationsDataSource dataSource;

	
	// Set everything up
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		hintText = (TextView)findViewById(R.id.hintText);
		fragmentManager = getFragmentManager();
		dataSource = new LocationsDataSource(this);
		dataSource.open();
		List<ObjectLocation> locations = dataSource.getAllObjectLocations();
		ArrayAdapter<ObjectLocation> adapter = new ArrayAdapter<ObjectLocation>(this, android.R.layout.simple_list_item_1, locations);
		setListAdapter(adapter);

		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setLongClickable(true);
		deleteOnLongClick(listView, dataSource);
		assignOnClickHandlerForObjects(listView);
		checkIfThereAreNoObjects();
	}

	// Show a dialog when the user attempts to delete an item, confirming with them. Items are
	// deleted when the user long clicks on an item
	public void deleteOnLongClick(ListView listView, final LocationsDataSource dataSource)
	{
		final Context context = this;
		final ArrayAdapter<ObjectLocation> adapter = (ArrayAdapter<ObjectLocation>) getListAdapter();
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id)
			{
				new AlertDialog.Builder(context).setTitle(getString(R.string.Delete))
				.setMessage(getString(R.string.DeleteText))
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dataSource.deleteLocation(adapter.getItem(position));
						adapter.remove(adapter.getItem(position));
						adapter.notifyDataSetChanged();
						checkIfThereAreNoObjects();

						
					}
				}).setNegativeButton(android.R.string.no, null).show();

				return false;
			}
		});
		
	}

	// This method assigns the onclick handlers for when the uesr selects an item from the list
	private void assignOnClickHandlerForObjects(ListView listView)
	{
		final ArrayAdapter<ObjectLocation> adapter = (ArrayAdapter<ObjectLocation>) getListAdapter();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			{
				ObjectLocation item = adapter.getItem(position);
				if (item.getType() == ObjectLocation.TYPE_LARGE_OBJECT)
				{
					startCompass(item.getLatitude(), item.getLongitude(), item.getImagePath());
				}
				else
				{
					startSmallImageView(item.getId(), item.getName());
				}
			}
		});
	}

	// Not used
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	// This method starts the compass intent
	public void startCompass(double destLatitude, double destLongitude, String imagePath)
	{
		Intent compassIntent = new Intent(this, Compass.class);

		compassIntent.putExtra("destLat", destLatitude);
		compassIntent.putExtra("destLong", destLongitude);
		compassIntent.putExtra("imagePath", imagePath);

		startActivityForResult(compassIntent, REQUEST_CODE_COMPASS);
	}

	// This method starts the small image view intent
	public void startSmallImageView(long id, String itemName)
	{
		Intent smallImageViewIntent = new Intent(this, ViewSmallObject.class);
		smallImageViewIntent.putExtra("name", itemName);
		smallImageViewIntent.putExtra("id", id);
		startActivity(smallImageViewIntent);
	}

	// This method handles when returning from an activity with a result
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ArrayAdapter<ObjectLocation> adapter = (ArrayAdapter<ObjectLocation>) getListAdapter();

		if (resultCode == RESULT_OK)
		{
			String name, description, imagePath;
			double latitude, longitude;
			switch (requestCode)
			{
			case REQUEST_CODE_CREATE_SMALL:
				name = data.getStringExtra("name");
				description = data.getStringExtra("description");
				imagePath = data.getStringExtra("imagePath");
				ObjectLocation location = dataSource.createSmallObject(name, description, imagePath, -1);
				adapter.add(location);
				break;
			case REQUEST_CODE_CREATE_LARGE:
				name = data.getStringExtra("name");
				imagePath = data.getStringExtra("imagePath");
				latitude = data.getDoubleExtra("latitude", 0.0d);
				longitude = data.getDoubleExtra("longitude", 0.0d);

				location = dataSource.createLargeObject(name, latitude, longitude, imagePath, -1);
				adapter.add(location);
				break;
			}
			checkIfThereAreNoObjects();
			adapter.notifyDataSetChanged();
		}
	}

	// This method is what happens when the add object button is clicked
	public void onClick(View view)
	{
		CreateObjectDialog create = new CreateObjectDialog();
		create.show(fragmentManager, null);
	}
	
	
	// This method checks to see if there are any items in the list. If not, the hint 
	// text is displayed. If so, the list view is displayed.
	public void checkIfThereAreNoObjects()
	{
		ListView listView = (ListView) findViewById(android.R.id.list);
		ArrayAdapter<ObjectLocation> adapter = (ArrayAdapter<ObjectLocation>) getListAdapter();
		Log.d("",String.valueOf(adapter.getCount()));
		if (adapter.getCount() == 0)
		{
			listView.setVisibility(View.GONE);
			hintText.setVisibility(View.VISIBLE);
		}
		else
		{
			listView.setVisibility(View.VISIBLE);
			hintText.setVisibility(View.GONE);
		}
	}
	
}
