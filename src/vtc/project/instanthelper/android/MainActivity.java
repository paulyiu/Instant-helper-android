package vtc.project.instanthelper.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends BaseActivity implements OnItemClickListener,
		OnClickListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle drawerListener;
	private MyAdapter myAdapter;
	private ImageView mUserIcon;
	private TextView mUserName;

	private Button mSignOutButton;
	private Button mRevokeButton;
	
	String regid;
	GoogleCloudMessaging gcm;
	String SENDER_ID = "42778666610";
	//String SERVER_URL = "http://10.0.3.2:8888";
	String SERVER_URL = "http://instant-helper.appspot.com";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		mGoogleApiClient = buildGoogleApiClient();
		
		if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);


        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
		
		mUserIcon = (ImageView) findViewById(R.id.user_icon);
		mUserName = (TextView) findViewById(R.id.user_displayname);
		mRevokeButton = (Button) findViewById(R.id.revoke_access_button);
		mSignOutButton = (Button) findViewById(R.id.sign_out_button);
		mRevokeButton.setOnClickListener(this);
		mSignOutButton.setOnClickListener(this);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerListener = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}
		};
		mDrawerLayout.setDrawerListener(drawerListener);

		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		myAdapter = new MyAdapter(this);
		mDrawerList.setAdapter(myAdapter);

		/*
		 * mDrawerList.setAdapter(new ArrayAdapter<>(this,
		 * android.R.layout.simple_list_item_1, mNavigationDrawerItemTitles));
		 */
		mDrawerList.setOnItemClickListener(this);
		selectItem(0);
		mDrawerList.setItemChecked(0, true);
		mDrawerList.setSelection(0);
	}
	
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID = " + regid;
					sendRegistrationIdToBackend();
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error: " + ex.getMessage();
				}

				return msg;
			}

			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}
	
	private void sendRegistrationIdToBackend() {
	   HttpClient httpClient = new DefaultHttpClient();
	   HttpPost httpPost = new HttpPost(SERVER_URL+"/register");
	    List<NameValuePair> params = new ArrayList<NameValuePair>(2);
	    params.add(new BasicNameValuePair("email",Plus.AccountApi.getAccountName(mGoogleApiClient)));
	    params.add(new BasicNameValuePair("regid",regid));
	    
	    try{
	    	httpPost.setEntity(new UrlEncodedFormEntity(params));
	    }catch(UnsupportedEncodingException e){
	    	e.printStackTrace();
	    }
	    
	    try{
	    	HttpResponse response = httpClient.execute(httpPost);
	    	Log.d("Http Post Response:", response.toString());
	    }catch(ClientProtocolException e){
	    	e.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}

	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_out_button:
			selectItem(6);
			break;
		case R.id.revoke_access_button:
			selectItem(7);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		if (drawerListener.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerListener.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		selectItem(position);

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		new LoadProfileImage(mUserIcon).execute(currentUser.getImage().getUrl()
				+ "&sz=350");
		mUserName.setText(currentUser.getDisplayName());
       // if (regid.isEmpty()) {
            registerInBackground();
        //}

	}

	public void selectItem(int position) {
		getActionBar()
				.setTitle(
						getResources()
								.getStringArray(R.array.nav_drawer_titles)[position]);
		Fragment fragment = null;
		Intent mainIntent = new Intent(this, FullscreenSignin.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		switch (position) {
		case 0:
			fragment = new FragmentHome();
			break;
		case 1:
			fragment = new FragmentOrder();
			break;
		case 2:
			fragment = new FragmentMessage();
			break;
		case 3:
			fragment = new FragmentContact();
			break;
		case 4:
			fragment = new FragmentProfile();
			break;
		case 6:
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			startActivity(mainIntent);
			break;
		case 7:
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
			mGoogleApiClient = buildGoogleApiClient();
			mGoogleApiClient.connect();
			startActivity(mainIntent);
			break;
		default:
			mDrawerLayout.closeDrawer(mDrawerList);
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}
}

class MyAdapter extends BaseAdapter {
	String[] titles;
	TypedArray images;
	private Context context;

	public MyAdapter(Context context) {
		this.context = context;

		titles = context.getResources().getStringArray(
				R.array.nav_drawer_titles);
		images = context.getResources().obtainTypedArray(
				R.array.nav_drawer_icons);
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		return titles[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listview_item_row, parent, false);
		} else {
			row = convertView;
		}
		TextView titleTextView = (TextView) row.findViewById(R.id.textView1);
		ImageView titleImageView = (ImageView) row
				.findViewById(R.id.imageView1);
		titleTextView.setText(titles[position]);
		titleImageView.setImageResource(images.getResourceId(position, -1));

		return row;
	}
}
