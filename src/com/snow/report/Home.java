package com.snow.report;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends ListActivity {
	// Fields
	private SnowReportListAdapter adap;
	static LinkedList<SnowReport> snowReport;

	// Properties
	private static LinkedList<SnowReport> getSnowReport() {
		if (snowReport == null) {
			try {
				snowReport = SnowReport.getSnowReport();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return snowReport;
	}

	// Methods
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.main);
		adap = new SnowReportListAdapter(this);
		setListAdapter(adap);

		// setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
		// getSnowReport()));

	}

	// Create optionMenu item
	// http://www.brighthub.com/mobile/google-android/articles/28673.aspx
	// http://mobiforge.com/designing/story/understanding-user-interface-android-part-4-even-more-views
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add("Refresh");		
		// menu.add(0,EDIT_CONTACT,0,"Edit Contact");
		
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {    
         // return MenuChoice(item);
		try {
			snowReport = null;
			adap.notifyDataSetChanged();
			
			return true;
			
		} catch (Exception e) {
			
			return false;
		}
    }


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "Click-" + String.valueOf(position),
				Toast.LENGTH_SHORT).show();
	}

	public static class SnowReportListAdapter extends BaseAdapter implements
			Filterable {
		private LayoutInflater mInflater;
		private Bitmap mIcon1;
		private Context context;

		public SnowReportListAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;
			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, null);
				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();

				holder.snowIcon = (ImageView) convertView
						.findViewById(R.id.snowIcon);
				holder.resortLabel = (TextView) convertView
						.findViewById(R.id.resortLabel);
				holder.snowReportLabel = (TextView) convertView
						.findViewById(R.id.snowReportLabel);

				/*
				 * This is an example of an item click
				 * convertView.setOnClickListener(new OnClickListener() {
				 * private int pos = position;
				 * 
				 * @Override public void onClick(View v) {
				 * Toast.makeText(context, "Click-" + String.valueOf(pos),
				 * Toast.LENGTH_SHORT).show(); } });
				 */

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Set the text
			SnowReport currentReport = getSnowReport().get(position);
			holder.resortLabel.setText(currentReport.resortName);
			holder.snowReportLabel.setText(currentReport.snowReport);

			// Set Current Image
			if (currentReport.weatherImage != null) {
				Drawable drawable = LoadImageFromWebOperations(currentReport.weatherImage);
				holder.snowIcon.setImageDrawable(drawable);
			}

			// Get flag name and id
			/*
			 * String filename = "flag_" + String.valueOf(position); int id =
			 * context.getResources().getIdentifier(filename, "drawable",
			 * context.getString(R.string.package_str)); // Icons bound to the
			 * rows. if (id != 0x0) { mIcon1 =
			 * BitmapFactory.decodeResource(context.getResources(), id); } //
			 * Bind the data efficiently with the holder.
			 * holder.snowIcon.setImageBitmap(mIcon1); holder..setText("flag " +
			 * String.valueOf(position));
			 */

			return convertView;
		}

		// http://www.androidpeople.com/android-load-image-from-url-example/
		private Drawable LoadImageFromWebOperations(String url) {
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				Drawable d = Drawable.createFromStream(is, "src name");
				return d;
			} catch (Exception e) {
				System.out.println("Exc=" + e);
				return null;
			}
		}

		static class ViewHolder {
			TextView snowReportLabel;
			TextView resortLabel;
			ImageView snowIcon;
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getCount() {
			return getSnowReport().size();
		}

		@Override
		public Object getItem(int position) {
			return getSnowReport().get(position);
		}
	}
}