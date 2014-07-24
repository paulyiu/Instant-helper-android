package vtc.project.instanthelper.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle drawerListener;
	private MyAdapter myAdapter;
	private ImageView mUserIcon;
	private TextView mUserName;
	
	private Button mSignOutButton;
	private Button mRevokeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGoogleApiClient = buildGoogleApiClient();
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
	
	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.sign_out_button:
			selectItem(5);
			break;
		case R.id.revoke_access_button:
			selectItem(6);
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
	public void onConnected(Bundle connectionHint){
		super.onConnected(connectionHint);
		
		Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		new LoadProfileImage(mUserIcon).execute(currentUser.getImage().getUrl()
				+ "&sz=350");
		mUserName.setText(currentUser.getDisplayName());

	}
	public void selectItem(int position) {
		getActionBar().setTitle(getResources().getStringArray(R.array.nav_drawer_titles)[position]);
		Fragment fragment = null;
		Intent mainIntent = new Intent(this, FullscreenSignin.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		switch(position){
		case 0:
			fragment = new FragmentHome();
			break;
		case 1:
			fragment = new FragmentFeatures();
			break;
		case 2:
			fragment = new FragmentMedia();
			break;
		case 3:
			fragment = new FragmentContact();
			break;
		case 5:
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
    		startActivity(mainIntent);
            break;
		case 6:
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            mGoogleApiClient = buildGoogleApiClient();
            mGoogleApiClient.connect();
    		startActivity(mainIntent);
    		break;
			default:
				mDrawerLayout.closeDrawer(mDrawerList);
		}
		
		if(fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}
}

class MyAdapter extends BaseAdapter {
	String[] socialSites;
	TypedArray images;
	private Context context;

	public MyAdapter(Context context) {
		this.context = context;

		socialSites = context.getResources().getStringArray(
				R.array.nav_drawer_titles);
		images = context.getResources().obtainTypedArray(
				R.array.nav_drawer_icons);
	}

	@Override
	public int getCount() {
		return socialSites.length;
	}

	@Override
	public Object getItem(int position) {
		return socialSites[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listview_item_row,parent,false);
		} else {
			row = convertView;
		}
		TextView titleTextView = (TextView) row.findViewById(R.id.textView1);
		ImageView titleImageView = (ImageView) row.findViewById(R.id.imageView1);
		titleTextView.setText(socialSites[position]);
		titleImageView.setImageResource(images.getResourceId(position, -1));
		
		return row;
	}
}
