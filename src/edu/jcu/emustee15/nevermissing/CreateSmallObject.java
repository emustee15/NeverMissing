package edu.jcu.emustee15.nevermissing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateSmallObject extends Activity
{
	// With help from http://developer.android.com/training/camera/photobasics.html
	
	private final static int REQUEST_PHOTO = 6;
	private ImageView photoView;
	private String imagePath;
	private EditText name;
	private EditText description;
	private FragmentManager fragmentManager;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_small_object);
		photoView = (ImageView)findViewById(R.id.photoView);
		name = (EditText)findViewById(R.id.nameEditText);
		description = (EditText)findViewById(R.id.descriptionEditText);
		fragmentManager = getFragmentManager();
		
		// Check to see if a name was bundled.
		try
		{
			String givenName = getIntent().getExtras().getString("name");
			if (givenName != null)
			{
				name.setText(givenName);
				name.setFocusable(false);
			}
		}
		catch (NullPointerException NPEx)
		{
			
		}
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_small_object, menu);
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
	
	public void okayClick(View v)
	{
		if (name.getText() == null || name.getText().toString().equals(""))
		{
			EmptyFieldDialog empty = new EmptyFieldDialog();
			empty.show(fragmentManager, null);
			return;
		}
		
		if (description.getText() == null || description.getText().toString().equals(""))
		{
			description.setText(getResources().getString(R.string.blankDescription));
		}
		
		this.getIntent().putExtra("name", name.getText().toString());
		this.getIntent().putExtra("description", description.getText().toString());
		
		if (imagePath!=null)
		{
			this.getIntent().putExtra("imagePath", imagePath);
		}
		else
		{
			this.getIntent().putExtra("imagePath", "NULL");
		}
		setResult(RESULT_OK, getIntent());
		finish();
	}

}
