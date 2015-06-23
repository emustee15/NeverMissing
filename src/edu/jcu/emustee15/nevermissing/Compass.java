package edu.jcu.emustee15.nevermissing;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends Activity implements SensorEventListener, LocationListener
{
	// Some code has been used from:
	// http://www.javacodegeeks.com/2013/09/android-compass-code-example.html

	private SensorManager sensorManager;
	private TextView distanceText;

	private double destLatitude;
	private double destLongitude;
	private double currentLatitude;
	private double currentLongitude;

	private String imagePath;
	private final static double RADIUS_OF_EARTH = 6367444.7f; // measured in
																// meters
	private final static float METERS_TO_FEET = 3.28084f;
	private float degreeToNorth;

	private final static int TIME_TO_UPDATE = 1000;
	private final static float MIN_DISTANCE_TO_UPDATE = .5f;

	private ImageView arrow;

	private String provider;
	private LocationManager locationManager;

	private FragmentManager fragmentManager;
	private boolean hasLocation = false;

	@Override
	
	// This class points the user to a specific latitude and longitude
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);
		arrow = (ImageView) findViewById(R.id.bigArrow);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		distanceText = (TextView) findViewById(R.id.distanceText);
		destLatitude = getIntent().getExtras().getDouble("destLat");
		destLongitude = getIntent().getExtras().getDouble("destLong");
		imagePath = getIntent().getExtras().getString("imagePath");

		fragmentManager = getFragmentManager();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = locationManager.getBestProvider(criteria, false);
		
		if (imagePath == null || imagePath.equals("NULL"))
		{
			Button viewImageButton = (Button)findViewById(R.id.viewImageButtonLargeObject);
			viewImageButton.setEnabled(false);
		}
	}

	// Not used
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compass, menu);
		return true;
	}

	// Not used
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub
	}

	// When pausing, unregister our listeners to conserve battery
	@Override
	public void onPause()
	{
		super.onPause();
		hasLocation = false;
		sensorManager.unregisterListener(this);
		locationManager.removeUpdates(this);
	}
	
	// Make sure to re-register listeners when resuming
	@Override
	public void onResume()
	{
		super.onResume();
		hasLocation = false;
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
		checkProviderIsEnabled();
	}

	// This method forces the user to keep GPS on. If GPS is off, this will create a dialog asking them
	// to turn it on. It will even help them by bring them to the location settings.
	private void checkProviderIsEnabled()
	{
		distanceText.setText(R.string.waitForLocation);
		hasLocation = false;

		if (provider != null && locationManager.isProviderEnabled(provider))
		{
			locationManager.requestLocationUpdates(provider, TIME_TO_UPDATE, MIN_DISTANCE_TO_UPDATE, this);
		}
		else if (!locationManager.isProviderEnabled(provider))
		{
			LocationDisabledDialog disabled = new LocationDisabledDialog();
			disabled.show(fragmentManager, null);
		}

		arrow.setImageDrawable(getResources().getDrawable(R.drawable.unknown));
	}
	
	// This method is called when the user clicks on the button to view the image
	public void onViewImageClicked(View v)
	{
		if (imagePath!= null && !imagePath.equals("NULL"))
		{
			Intent viewLargerImage = new Intent(this, ViewLargeImage.class);
			viewLargerImage.putExtra("imagePath", imagePath);
			startActivity(viewLargerImage);
		}
	}

	// This method is called when the user rotates the phone
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (hasLocation)
		{
			float previousDegree = getAngleToDestination();
			degreeToNorth = event.values[0];

			RotateAnimation rotatingArrow = new RotateAnimation(previousDegree, getAngleToDestination(), Animation.RELATIVE_TO_SELF, .5f,
					Animation.RELATIVE_TO_SELF, .5f);
			rotatingArrow.setDuration(150);
			rotatingArrow.setFillAfter(true);

			arrow.startAnimation(rotatingArrow);
			
		}
		else
		{
			RotateAnimation rotatingArrow = new RotateAnimation(0, 0, Animation.RELATIVE_TO_SELF, .5f,
					Animation.RELATIVE_TO_SELF, .5f);
			rotatingArrow.setDuration(150);
			rotatingArrow.setFillAfter(true);
			arrow.startAnimation(rotatingArrow);
		}
	}

	// This method uses some trig to calculate the distance between two coordinates on earth
	public double getDistanceToDestination()
	{
		double dLaditude = destLatitude - currentLatitude;
		double dLongitude = destLongitude - currentLongitude;

		dLaditude *= (Math.PI / 180);
		dLongitude *= (Math.PI / 180);

		double currentLatitudeInRadians = currentLatitude * (Math.PI / 180);
		double destLatitudeInRadians = destLatitude * (Math.PI / 180);

		// Used help from
		// http://stackoverflow.com/questions/365826/calculate-distance-between-2-gps-coordinates
		double d = (Math.sin(dLaditude / 2) * Math.sin(dLaditude / 2f) + Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2f)
				* Math.cos(currentLatitudeInRadians) * Math.cos(destLatitudeInRadians));
		double dRadians = (2 * Math.atan2(Math.sqrt(d), Math.sqrt(1 - d)));

		return dRadians * RADIUS_OF_EARTH;
	}

	
	// This method is called when the location changes. 
	@Override
	public void onLocationChanged(Location location)
	{
		currentLatitude = location.getLatitude();
		currentLongitude = location.getLongitude();
		double distance = getDistanceToDestination()*METERS_TO_FEET;
		String formattedDistance = String.format("%.1f", distance);
		distanceText.setText(formattedDistance + " " + getResources().getString(R.string.feet));

		if (!hasLocation)
		{
			arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow));
		}
		hasLocation = true;
	}

	// This method is called when the user disables a provider
	@Override
	public void onProviderDisabled(String provider)
	{
		hasLocation = false;
		if (provider.equals(this.provider))
		{
			checkProviderIsEnabled();
		}

	}

	// Not used
	@Override
	public void onProviderEnabled(String provider)
	{
		checkProviderIsEnabled();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// not used

	}

	// This method gets the angle between two points
	public float getAngleToDestination()
	{
		double dx = destLongitude - currentLongitude;
		double dy = destLatitude - currentLatitude;

		// The atan2 function is really nice!
		return (float)(Math.atan2(-dy, dx)*180/Math.PI) - degreeToNorth + 90;

	}

}
