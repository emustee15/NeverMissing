package edu.jcu.emustee15.nevermissing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateLargeObject extends Activity implements LocationListener
{

	private FragmentManager fragmentManager;
	private LocationManager locationManager;
	private final static int TIME_TO_UPDATE = 1000;
	private final static float MIN_DISTANCE_TO_UPDATE = .5f;
	private String provider;
	private final static float METERS_TO_FEET = 3.28084f;
	private TextView GPSStatus;
	private String imagePath;
	private boolean hadLocation = false;
	private EditText name;
	private ImageView photoView;
	private final static int REQUEST_PHOTO = 8;

	private double currentLatitude;
	private double currentLongitude;

	// This class is used when creating a large object
	
	@Override
	
	// Set up all the necessary things here
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_large_object);

		photoView = (ImageView)findViewById(R.id.photoView);
		fragmentManager = getFragmentManager();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = locationManager.getBestProvider(criteria, false);
		GPSStatus = (TextView) findViewById(R.id.GPSStatus);
		checkProviderIsEnabled();
		name = (EditText)findViewById(R.id.largeNameEditText);
		
	}

	// Not used
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_large_object, menu);
		return true;
	}
	
	public void photoButton(View v)
	{
		Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (photoIntent.resolveActivity(getPackageManager()) != null)
		{
			startActivityForResult(photoIntent, REQUEST_PHOTO);
		}
	}
	
	// This method is used for when the user returns from taking a photo
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_PHOTO)
		{
			if (resultCode == RESULT_OK)
			{
				createImageFile((Bitmap)data.getExtras().get("data"));
				photoView.setImageURI(Uri.fromFile(new File(imagePath)));
			}
		}
	}
	
	// This method saves a bitmap to a file
	private File createImageFile(Bitmap bitmap)
	{
		File image = null;
		try 
		{
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(new Date());
			String imageFileName = "NeverMissing_" + timeStamp + "_";
			File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			image = new File(storageDir, imageFileName + ".jpg");
			imagePath = image.getAbsolutePath();
			
			FileOutputStream out = new FileOutputStream(image);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		}
		catch (IOException ex)
		{
			Toast.makeText(this, getResources().getString(R.string.fileCreateError), Toast.LENGTH_LONG).show();
		}
		
		return image;
	}

	// This method makes sure the user has GPS on
	private void checkProviderIsEnabled()
	{

		if (provider != null && locationManager.isProviderEnabled(provider))
		{
			locationManager.requestLocationUpdates(provider, TIME_TO_UPDATE, MIN_DISTANCE_TO_UPDATE, this);
		}
		else if (!locationManager.isProviderEnabled(provider))
		{
			LocationDisabledDialog disabled = new LocationDisabledDialog();
			disabled.show(fragmentManager, null);
		}

	}

	// This method is called when the location is changed
	@Override
	public void onLocationChanged(Location location)
	{
		currentLatitude = location.getLatitude();
		currentLongitude = location.getLongitude();
		float accuarcy = location.getAccuracy() * METERS_TO_FEET;
		String formattedAccuarcy = String.format("%.1f", accuarcy);
		GPSStatus.setText(getString(R.string.accuaracy) + " " + formattedAccuarcy + " " + getString(R.string.feet));
		hadLocation = true;
	}
	
	// This method is called when the user disables a location setting. Hopefully it was
	// not GPS or they are going to get a dialog telling them to reenable GPS. 
	@Override
	public void onProviderDisabled(String provider)
	{
		if (provider.equals(this.provider))
		{
			checkProviderIsEnabled();
		}

	}

	// Not used
	@Override
	public void onProviderEnabled(String provider)
	{
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// Not used
	}

	// This method is called when the user presses the add object button
	public void addClick(View v)
	{
		
		if (hadLocation)
		{
			if (name.getText() == null || name.getText().toString() == "")
			{
				new EmptyFieldDialog().show(fragmentManager, null);
			}
			this.getIntent().putExtra("name", name.getText().toString());
			this.getIntent().putExtra("latitude", currentLatitude);
			this.getIntent().putExtra("longitude", currentLongitude);
			if (imagePath == null)
			{
				this.getIntent().putExtra("imagePath", "NULL");
			}
			else
			{
				this.getIntent().putExtra("imagePath", imagePath);
			}
			setResult(RESULT_OK, getIntent());
			finish();
		}
		else
		{
			new MissingLocationDialog().show(fragmentManager, null);
		}
	}

}
