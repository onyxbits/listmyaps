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

	/**
	 * Maximum items per category (for sorting)
	 */
	public static final int MAXCATEGORY=10000;
	
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
	public int rating;
	public String dataDir;
	public String comment;
	public int category;
	public int targetsdk;

	public SortablePackageInfo(){}

	@Override
	public int compareTo(SortablePackageInfo another) {
		return MAXCATEGORY*category+ displayName.compareTo(another.displayName);
	}

	@Override
	public void onClick(View v) {
		selected = ((CheckBox) v).isChecked();
	}

}
