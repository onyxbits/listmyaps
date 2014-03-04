package de.onyxbits.listmyapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database schema definitions
 * 
 * @author patrick
 * 
 */
public class Schema extends SQLiteOpenHelper {

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PACKID = "packageid";

	public static final String TABLE_TEMPLATES = "templates";
	public static final String COLUMN_TNAME = "templatename";
	public static final String COLUMN_HEADER = "header";
	public static final String COLUMN_FOOTER = "footer";
	public static final String COLUMN_ITEM = "item";

	public static final String TABLE_ANOTATIONS = "annotations";
	public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_TAGS = "tags";
	public static final String COLUMN_RATING = "rating";

	public static final String TABLE_SELECTION = "selections";
	public static final String COLUMN_SELECTED = "selected";
	public static final String COLUMN_SLOTID = "slotid";

	private static final String DATABASE_NAME = "lma.db";
	private static final int DATABASE_VERSION = 2;

	private Context context;

	public Schema(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		onUpgrade(database, 0, 1); // NOTE: versioning began at 1 not 0.
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion+1) {
			case 1: {
				v1(db);
			}
			case 2: {
				v2(db);
			}
		}
	}

	private void v1(SQLiteDatabase database) {
		database.execSQL("create table " + TABLE_TEMPLATES + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_TNAME
				+ " text not null, " + COLUMN_HEADER + " text not null, "
				+ COLUMN_FOOTER + " text not null, " + COLUMN_ITEM
				+ " text not null); ");

		database.execSQL("create table " + TABLE_ANOTATIONS + "(" + COLUMN_PACKID
				+ " text not null, " + COLUMN_COMMENT + " text not null, "
				+ COLUMN_SELECTED + " integer, " + COLUMN_RATING + " integer,"
				+ COLUMN_CATEGORY + " integer);");

		database.execSQL("create table " + TABLE_SELECTION + "(" + COLUMN_PACKID
				+ " text not null, " + COLUMN_SLOTID + " integer, " + COLUMN_SELECTED
				+ " integer);");

		ContentValues values = new ContentValues();
		String[] titles = context.getResources().getStringArray(
				R.array.stdformattitles);
		String[] headers = context.getResources()
				.getStringArray(R.array.stdheaders);
		String[] formats = context.getResources()
				.getStringArray(R.array.stdformats);
		String[] footers = context.getResources()
				.getStringArray(R.array.stdfooters);
		for (int i = 0; i < formats.length; i++) {
			values.put(COLUMN_TNAME, titles[i]);
			values.put(COLUMN_HEADER, headers[i]);
			values.put(COLUMN_ITEM, formats[i]);
			values.put(COLUMN_FOOTER, footers[i]);
			database.insert(Schema.TABLE_TEMPLATES, null, values);
			values.clear();
		}
	}

	private void v2(SQLiteDatabase db) {
		db.execSQL("alter table " + TABLE_ANOTATIONS + " ADD COLUMN " + COLUMN_TAGS
				+ " text;");
	}
}