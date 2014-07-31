package vtc.project.instanthelper.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentContact extends Fragment implements OnClickListener {
	LinearLayout panelBackgroud, panelContact;
	TextView tvBackgroud, tvContactUs;
	public FragmentContact(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
		//TextView textView = (TextView) rootView.findViewById(R.id.contact_title_backgroud);
		//textView.setText(Html.fromHtml(getString(R.string.contact_desc)));
		panelBackgroud = (LinearLayout) rootView.findViewById(R.id.contact_panel_backgroud);
		panelContact = (LinearLayout) rootView.findViewById(R.id.contact_panel_contact);
		panelBackgroud.setVisibility(View.VISIBLE);
		panelContact.setVisibility(View.GONE);
		tvBackgroud = (TextView) rootView.findViewById(R.id.contact_title_backgroud);
		tvContactUs = (TextView) rootView.findViewById(R.id.contact_title_contact);
		
		tvBackgroud.setOnClickListener(this);
		tvContactUs.setOnClickListener(this);
		return rootView;
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.contact_title_backgroud:
		case R.id.contact_title_contact:
			panelBackgroud.setVisibility(panelBackgroud.isShown()?View.GONE:View.VISIBLE);
			panelContact.setVisibility(panelContact.isShown()?View.GONE:View.VISIBLE);
			break;
		}

		
	}

}
