package edu.jcu.emustee15.nevermissing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class LocationDisabledDialog extends DialogFragment
{
	
	// This class is used when GPS is disabled. It asks the user to re-enable
	// GPS or asks them to cancel the activity.
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setMessage(R.string.locationDisabledError);
		builder.setTitle(R.string.locationDisabledErrorTitle);
		builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				getActivity().finish();
			}
		});
		return builder.create();
	}
}
