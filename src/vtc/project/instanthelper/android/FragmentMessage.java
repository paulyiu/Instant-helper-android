package vtc.project.instanthelper.android;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentMessage extends Fragment implements OnItemClickListener {

	ListView listView;

	public FragmentMessage() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);
		listView = (ListView) rootView.findViewById(R.id.message_listview);
		listView.setAdapter(new MessageAdapter(getActivity()));
		listView.setOnItemClickListener(this);
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		selectItem(position);
		

	}

	private void selectItem(int position) {
		TextView tvCompany, tvPrice, tvPerson, tvPhone;
		String[] titles, price, person, phone;
		Context context = getActivity();
		titles = context.getResources().getStringArray(R.array.message_company);
		price = context.getResources().getStringArray(R.array.message_price);
		person = context.getResources().getStringArray(R.array.message_person);
		phone = context.getResources().getStringArray(R.array.message_phone);

		final Dialog dialog = new Dialog(FragmentMessage.this.getView().getContext());
		dialog.setContentView(R.layout.dialog_message);
		dialog.setTitle(titles[position]);
		tvCompany = (TextView) dialog.findViewById(R.id.message_dialog_company);
		tvPrice = (TextView) dialog.findViewById(R.id.message_dialog_price);
		tvPerson = (TextView) dialog.findViewById(R.id.message_dialog_person);
		tvPhone = (TextView) dialog.findViewById(R.id.message_dialog_phone);
		tvCompany.setText(titles[position]);
		tvPrice.setText("$"+price[position]);
		tvPerson.setText(person[position]);
		tvPhone.setText(phone[position]);

		Button btnClose = (Button) dialog
				.findViewById(R.id.message_dialog_close);
		btnClose.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.cancel();
				return false;
			}
		});

		dialog.show();
		Toast.makeText(context, "dialog show at " + position, Toast.LENGTH_LONG);
	}

}

class MessageAdapter extends BaseAdapter {
	String[] titles, price, person, phone;
	private Context context;

	public MessageAdapter(Context context) {
		this.context = context;

		titles = context.getResources().getStringArray(R.array.message_company);
		price = context.getResources().getStringArray(R.array.message_price);
		person = context.getResources().getStringArray(R.array.message_person);
		phone = context.getResources().getStringArray(R.array.message_phone);

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
			row = inflater
					.inflate(R.layout.listview_message_row, parent, false);
		} else {
			row = convertView;
		}
		TextView titleTextView = (TextView) row
				.findViewById(R.id.message_row_company);
		TextView priceTextView = (TextView) row
				.findViewById(R.id.message_row_price);

		titleTextView.setText(titles[position]);
		priceTextView.setText("$" + price[position]);

		return row;
	}
}