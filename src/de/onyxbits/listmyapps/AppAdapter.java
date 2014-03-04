package de.onyxbits.listmyapps;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * For mapping a SortablePackageInfo into a view.
 * 
 * @author patrick
 * 
 */
public class AppAdapter extends ArrayAdapter<SortablePackageInfo> {

	private int layout;

	public AppAdapter(Context context, int textViewResourceId,
			List<SortablePackageInfo> spi, int layout) {
		super(context, textViewResourceId, spi);
		this.layout = layout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View ret = convertView;
		if (ret == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			ret = inflater.inflate(layout, null);
		}
		SortablePackageInfo spi = getItem(position);

		((TextView) ret.findViewById(R.id.appname)).setText(spi.displayName);
		((TextView) ret.findViewById(R.id.apppackage)).setText(spi.packageName);
		((ImageView) ret.findViewById(R.id.icon)).setImageDrawable(spi.icon);

		switch (layout) {
			case R.layout.app_item: {
				CheckBox sel = ((CheckBox) ret.findViewById(R.id.selected));
				sel.setChecked(spi.selected);
				sel.setOnClickListener(spi);
				break;
			}
			case R.layout.app_item_annotation: {
				TextView comment = (TextView) ret.findViewById(R.id.comments);
				TextView tags = (TextView) ret.findViewById(R.id.tags);
				String tmp = MainActivity.noNull(spi.tags);
				if (tmp.length() > 0) {
					tags.setText(tmp);
					tags.setVisibility(View.VISIBLE);
				}
				else {
					tags.setVisibility(View.GONE);
				}
				
				tmp = MainActivity.noNull(spi.comment);
				if (tmp.length() > 0) {
					comment.setText(tmp);
					comment.setVisibility(View.VISIBLE);
				}
				else {
					comment.setVisibility(View.GONE);
				}
			}
		}
		return ret;
	}

}
