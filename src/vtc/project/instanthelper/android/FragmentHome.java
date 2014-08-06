package vtc.project.instanthelper.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FragmentHome extends Fragment implements OnClickListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper mViewFlipper;
	private Context mContext;
	private final GestureDetector detector = new GestureDetector(
			new SwipeGestureDetector());

	public FragmentHome() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.viewflipper_home, container,
				false);
		mContext = getActivity();
		mViewFlipper = (ViewFlipper) rootView.findViewById(R.id.view_flipper);
		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
		mViewFlipper.setAutoStart(true);
		mViewFlipper.setFlipInterval(3000);
		mViewFlipper.startFlipping();

		rootView.findViewById(R.id.button_air).setOnClickListener(this);
		rootView.findViewById(R.id.button_fire).setOnClickListener(this);
		rootView.findViewById(R.id.button_water).setOnClickListener(this);
		rootView.findViewById(R.id.button_electric).setOnClickListener(this);

		TextView textView = (TextView) rootView.findViewById(R.id.desc_first);
		textView.setText(Html.fromHtml(getString(R.string.desc_first)));
		textView = (TextView) rootView.findViewById(R.id.desc_second);
		textView.setText(Html.fromHtml(getString(R.string.desc_second)));
		textView = (TextView) rootView.findViewById(R.id.desc_third);
		textView.setText(Html.fromHtml(getString(R.string.desc_third)));
		return rootView;
	}

	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.stopFlipping();
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.slide_in_left));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.slide_out_left));
					mViewFlipper.showNext();
					mViewFlipper.setFlipInterval(3000);
					mViewFlipper.startFlipping();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.stopFlipping();
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.slide_in_right));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.slide_out_right));
					mViewFlipper.showNext();
					mViewFlipper.setFlipInterval(3000);
					mViewFlipper.startFlipping();
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		Fragment fragment = null;
		switch(v.getId()){
		case R.id.button_air:
			fragment = new FragmentOrder();
			break;
		case R.id.button_fire:
			fragment = new FragmentOrder();
			break;
		case R.id.button_water:
			fragment = new FragmentOrder();
			break;
		case R.id.button_electric:
			fragment = new FragmentOrder();
			break;
		}
		
		if(fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
	}
}
