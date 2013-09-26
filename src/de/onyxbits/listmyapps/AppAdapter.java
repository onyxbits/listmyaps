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

		switch (layout) {
			case R.layout.app_item: {
				((TextView) ret.findViewById(R.id.appname)).setText(spi.displayName);
				((TextView) ret.findViewById(R.id.apppackage)).setText(spi.packageName);
				((ImageView) ret.findViewById(R.id.icon)).setImageDrawable(spi.icon);
				CheckBox sel = ((CheckBox) ret.findViewById(R.id.selected));
				sel.setChecked(spi.selected);
				sel.setOnClickListener(spi);
				break;
			}
			case R.layout.app_item_annotation: {
				((TextView) ret.findViewById(R.id.appname)).setText(spi.displayName);
				((TextView) ret.findViewById(R.id.apppackage)).setText(spi.packageName);
				((ImageView) ret.findViewById(R.id.icon)).setImageDrawable(spi.icon);
				((TextView) ret.findViewById(R.id.comments)).setText(MainActivity
						.noNull(spi.comment));
			}
		}

		return ret;

	}

}
