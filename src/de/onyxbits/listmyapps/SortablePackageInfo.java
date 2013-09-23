package de.onyxbits.listmyapps;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;

/**
 * Data container for holding all the relevant information on a package.
 * @author patrick
 * 
 */
class SortablePackageInfo implements Comparable<SortablePackageInfo>,
		View.OnClickListener {

	public String packageName;
	public String displayName;
	public String installer;
	public Drawable icon;
	public boolean selected;
	public int versionCode;
	public String version;
	public long firstInstalled;
	public long lastUpdated;
	public int uid;
	public String dataDir;
	public String comment;

	public SortablePackageInfo(){}

	@Override
	public int compareTo(SortablePackageInfo another) {
		return displayName.compareTo(another.displayName);
	}

	@Override
	public void onClick(View v) {
		selected = ((CheckBox) v).isChecked();
	}

}
