package edu.jcu.emustee15.nevermissing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class CreateObjectDialog extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// This method uses an alert dialog to create either a small object
		// or a large object. 
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setMessage(R.string.CreateObjectDialogText);
		builder.setTitle(R.string.CreateDialogTitle);
		builder.setPositiveButton(R.string.SmallButton, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent createSmallObject = new Intent(getActivity(), CreateSmallObject.class);
				getActivity().startActivityForResult(createSmallObject, Home.REQUEST_CODE_CREATE_SMALL);
				
			}
		});
		builder.setNegativeButton(R.string.LargeButton, new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent createLargeObject = new Intent(getActivity(), CreateLargeObject.class);
				getActivity().startActivityForResult(createLargeObject, Home.REQUEST_CODE_CREATE_LARGE);
			}
		});
		return builder.create();
	}
}
