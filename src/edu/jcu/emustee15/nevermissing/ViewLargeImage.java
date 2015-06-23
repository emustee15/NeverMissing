package edu.jcu.emustee15.nevermissing;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;

public class ViewLargeImage extends Activity
{
	

	// This class is used to display an image larger when the user
	// either clicks on the image in the small object viewer, or the 
	// view image button in the compass. 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_large_image);
		String imagePath = this.getIntent().getExtras().getString("imagePath");
		ImageView imageView = (ImageView)findViewById(R.id.largeImageViewer);
		imageView.setImageURI(Uri.fromFile(new File(imagePath)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_large_image, menu);
		return true;
	}

}
