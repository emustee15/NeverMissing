package edu.jcu.emustee15.nevermissing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class EmptyFieldDialog extends DialogFragment
{
	
	// This class is used when the user leaves the name field blank.
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setMessage(R.string.EmptyFieldDialogText);
		builder.setTitle(R.string.EmptyFieldTitle);
		builder.setPositiveButton(R.string.okay, null);
		return builder.create();
	}
}
