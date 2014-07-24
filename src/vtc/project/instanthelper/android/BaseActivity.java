package vtc.project.instanthelper.android;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class BaseActivity extends Activity implements ConnectionCallbacks,
OnConnectionFailedListener, View.OnClickListener{
	
	private static final String TAG = "vtc.project.instanthelper.android";
	public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private int mSignInProgress;
	private static final int STATE_DEFAULT = 0;
	private static final int STATE_SIGN_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;

	private static final int RC_SIGN_IN = 0;

	private static final int DIALOG_PLAY_SERVICES_ERROR = 0;
	private static final String SAVED_PROGRESS = "sign_in_progress";

	private GoogleApiClient mGoogleApiClient;
	private PendingIntent mSignInIntent;
	private int mSignInError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	private GoogleApiClient buildGoogleApiClient() {
		return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	protected void onSaveIntanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SAVED_PROGRESS, mSignInProgress);
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "onConnected:");

		mSignInProgress = STATE_DEFAULT;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "onConnectionFailed 01: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());

		if (mSignInProgress != STATE_IN_PROGRESS) {
			mSignInIntent = result.getResolution();
			mSignInError = result.getErrorCode();
			Log.i(TAG,
					"onConnectionFailed 02: mSignInProgress != STATE_IN_PROGRESS");

			if (mSignInProgress == STATE_SIGN_IN) {
				Log.i(TAG,
						"onConnectionFailed 03a: mSignInProgress == STATE_SIGN_IN:");
				resolveSignInError();
				Log.i(TAG, "onConnectionFailed 03b: after resolveSignInError()");
			}
		}

		onSignedOut();

	}

	private void resolveSignInError() {
		if (mSignInIntent != null) {
			try {
				mSignInProgress = STATE_IN_PROGRESS;
				startIntentSenderForResult(mSignInIntent.getIntentSender(),
						RC_SIGN_IN, new Intent(), 0, 0, 0);
				Log.i(TAG,
						"resolvSignInError(): startIntentSenderForResult(mSignInIntent.getIntentSender(), RC_SIGN_IN, new Intent(), 0, 0, 0);");
			} catch (SendIntentException e) {
				Log.i(TAG,
						"Sign in intent could not to send:"
								+ e.getLocalizedMessage());
				mSignInProgress = STATE_SIGN_IN;
				mGoogleApiClient.connect();
			}
		} else {
			showDialog(DIALOG_PLAY_SERVICES_ERROR);
			Log.i(TAG,
					"resolvSignInError(): showDialog(DIALOG_PLAY_SERVICES_ERROR)");

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RC_SIGN_IN:
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "resolvSignInError():resultCode == RESULT_OK");
				mSignInProgress = STATE_SIGN_IN;
			} else {
				mSignInProgress = STATE_DEFAULT;
				Log.i(TAG,
						"onActivityResult 01: mSignInProgress = STATE_DEFAULT resultCode: "
								+ resultCode);

			}

			if (!mGoogleApiClient.isConnected()) {
				mGoogleApiClient.connect();
				Log.i(TAG, "onActivityResult 02: !mGoogleApiClient.isConnected()");
			}
			break;
		}
	}

	private void onSignedOut() {
		Log.i(TAG, "onSignedOut() 01: ");

	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PLAY_SERVICES_ERROR:
			if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
				return GooglePlayServicesUtil.getErrorDialog(mSignInError,
						this, RC_SIGN_IN,
						new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								Log.e(TAG,
										"Google Play services resolution cancelled");
								mSignInProgress = STATE_DEFAULT;
							}
						});
			} else {
				return new AlertDialog.Builder(this)
						.setMessage(R.string.play_services_error)
						.setPositiveButton(R.string.close,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Log.e(TAG,
												"Google Play services error could not be resolved: "
														+ mSignInError);
									}
								}).create();
			}
		default:
			return super.onCreateDialog(id);
		}
	}

}
