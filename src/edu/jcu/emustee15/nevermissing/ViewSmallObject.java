package edu.jcu.emustee15.nevermissing;

import java.io.File;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewSmallObject extends ListActivity
{

	// This class allows the user to browse subobjects of a given small object
	// A subobject is a description of an objects location, either in image form,
	// text form, or both. 
	private LocationsDataSource dataSource;
	private long id;
	private ImageView imageView;
	private String name;
	private String currentImagePath;

	private final static int CREATE_SUB_OBJECT_REQUEST = 7;

	@Override
	// Set up everything here
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		id = getIntent().getExtras().getLong("id");
		name = getIntent().getExtras().getString("name");
		setContentView(R.layout.activity_view_small_object);
		dataSource = new LocationsDataSource(this);
		dataSource.open();
		List<ObjectLocation> locations = dataSource.getObjectsAssociatedWithId(id);
		final ArrayAdapter<ObjectLocation> adapter = new ArrayAdapter<ObjectLocation>(this, android.R.layout.simple_list_item_1, locations);
		setListAdapter(adapter);

		imageView = (ImageView) findViewById(R.id.smallObjectImageView);

		if (adapter.getCount() > 0)
		{
			String imagePath = adapter.getItem(0).getImagePath();
			setCurrentImage(imagePath);
		}
		else
		{
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.waitforcamera));
		}
		ListView listView = (ListView) findViewById(android.R.id.list);
		deleteOnLongClick(listView, dataSource);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			{
				ObjectLocation location = adapter.getItem(position);
				String imagePath = location.getImagePath();
				setCurrentImage(imagePath);
			}


		});
	}
	
	// This method sets the currentImagePath and the imageView. The currentImagePath
	// is used when the user clicks on the image to view a larger version. 
	private void setCurrentImage(String imagePath)
	{
		File imageFile = new File(imagePath);

		if (imageFile.exists())
		{
			imageView.setImageURI(Uri.fromFile(imageFile));
			currentImagePath = imagePath;
		}
		else
		{
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.waitforcamera));
			currentImagePath = null;
		}
	}

	// Not used
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_small_object, menu);
		return true;
	}

	// This method is called when the user wants to add a subobject
	public void addButton(View v)
	{
		Intent createSmallImageIntent = new Intent(this, CreateSmallObject.class);
		createSmallImageIntent.putExtra("name", name);
		startActivityForResult(createSmallImageIntent, CREATE_SUB_OBJECT_REQUEST);
	}

	// This method is called when returning from the create small object activity
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ArrayAdapter<ObjectLocation> adapter = (ArrayAdapter<ObjectLocation>) getListAdapter();
		if (requestCode == CREATE_SUB_OBJECT_REQUEST && resultCode == RESULT_OK)
		{
			String description = data.getStringExtra("description");
			String imagePath = data.getStringExtra("imagePath");
			ObjectLocation location = dataSource.createSmallObject(name, description, imagePath, id);
			location.setNameToDescription();
			adapter.add(location);
		}
		adapter.notifyDataSetChanged();
	}

	// This method confirms from the user when they delete an object from the list. 
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

						
					}
				}).setNegativeButton(android.R.string.no, null).show();

				return false;
			}
		});
	}
	
	// This method is called when the imageView is clicked.
	public void onImageClicked(View v)
	{
		if (currentImagePath != null)
		{
			Intent viewLargerImage = new Intent(this, ViewLargeImage.class);
			viewLargerImage.putExtra("imagePath", currentImagePath);
			startActivity(viewLargerImage);
		}
	}
}
