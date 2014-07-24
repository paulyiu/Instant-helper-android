package vtc.project.instanthelper.android;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentContact extends Fragment {
	
	public FragmentContact(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
		TextView textView = (TextView) rootView.findViewById(R.id.contact_desc);
		textView.setText(Html.fromHtml(getString(R.string.contact_desc)));
		return rootView;
	}

}
