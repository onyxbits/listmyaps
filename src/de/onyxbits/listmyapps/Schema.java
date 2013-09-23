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

	public static final String TABLE_FORMATS = "formats";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FNAME = "formatname";
	public static final String COLUMN_HEADER = "header";
	public static final String COLUMN_FOOTER = "footer";
	public static final String COLUMN_ITEM = "item";

	public static final String TABLE_ANOTATIONS = "annotations";
	public static final String COLUMN_PACKID = "packageid";
	public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_SORTPRIORITY = "sortpriority";

	private static final String DATABASE_NAME = "lma.db";
	private static final int DATABASE_VERSION = 1;

	private Context context;

	public Schema(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("create table " + TABLE_FORMATS + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_FNAME
				+ " text not null, " + COLUMN_HEADER + " text not null, " + COLUMN_FOOTER
				+ " text not null, " + COLUMN_ITEM + " text not null); ");
		database.execSQL("create table " + TABLE_ANOTATIONS + "(" + COLUMN_PACKID
				+ " text not null, " + COLUMN_COMMENT + " text not null, "
				+ COLUMN_SORTPRIORITY + " integer);");
		ContentValues values = new ContentValues();
		String[] titles = context.getResources().getStringArray(
				R.array.stdformattitles);
		String[] formats = context.getResources()
				.getStringArray(R.array.stdformats);
		for (int i = 0; i < formats.length; i++) {
			values.put(COLUMN_FNAME, titles[i]);
			values.put(COLUMN_HEADER, "");
			values.put(COLUMN_ITEM, formats[i]);
			values.put(COLUMN_FOOTER, "");
			database.insert(Schema.TABLE_FORMATS, null, values);
			values.clear();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(getClass().getName(), "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMATS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANOTATIONS);
		onCreate(db);
	}

}