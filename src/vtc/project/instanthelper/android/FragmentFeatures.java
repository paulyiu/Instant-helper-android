package vtc.project.instanthelper.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentFeatures extends Fragment {
	
	public FragmentFeatures(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_features, container, false);
		return rootView;
	}

}
