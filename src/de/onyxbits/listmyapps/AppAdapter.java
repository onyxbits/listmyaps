package de.onyxbits.listmyapps;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<SortablePackageInfo> {

	public AppAdapter(Context context, int textViewResourceId,
			List<SortablePackageInfo> spi) {
		super(context, textViewResourceId, spi);
	}
	
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View ret;
    SortablePackageInfo spi = getItem(position);
    
    LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    ret = inflater.inflate(R.layout.app_item,null);
    ((TextView)ret.findViewById(R.id.appname)).setText(spi.displayName);
    ((TextView)ret.findViewById(R.id.apppackage)).setText(spi.packageName);
    CheckBox sel = ((CheckBox)ret.findViewById(R.id.selected));
    sel.setOnClickListener(spi);
    sel.setChecked(spi.selected);
    return ret;

  }

}
