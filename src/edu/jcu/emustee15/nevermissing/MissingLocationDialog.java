package edu.jcu.emustee15.nevermissing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class MissingLocationDialog extends DialogFragment
{
	
	// This class is used when the user tries to add a large object without a valid location
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setMessage(R.string.noLocationError);
		builder.setTitle(R.string.EmptyFieldTitle);
		builder.setPositiveButton(R.string.okay, null);
		return builder.create();
	}
}
