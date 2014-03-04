package de.onyxbits.listmyapps;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AnnotationsSource {

	private Schema schema;
	private SQLiteDatabase database;
	private HashMap<String, String> comments;
	private HashMap<String, String> tags;

	public AnnotationsSource(Context ctx) {
		schema = new Schema(ctx);
		comments = new HashMap<String, String>();
		tags = new HashMap<String, String>();
	}

	public void open() throws SQLException {
		database = schema.getWritableDatabase();
		String[] all = {
				Schema.COLUMN_PACKID,
				Schema.COLUMN_COMMENT,
				Schema.COLUMN_TAGS };

		Cursor cursor = database.query(Schema.TABLE_ANOTATIONS, all, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String pname = cursor.getString(0);
			comments.put(pname, cursor.getString(1));
			tags.put(pname, cursor.getString(2));
			cursor.moveToNext();
		}
		cursor.close();
	}

	public String getComment(String packName) {
		return comments.get(packName);
	}

	public String getTags(String packName) {
		return tags.get(packName);
	}

	public void putComment(String packName, String comment) {
		comments.put(packName, comment);
		ContentValues values = new ContentValues();
		values.put(Schema.COLUMN_COMMENT, comment);
		if (database.update(Schema.TABLE_ANOTATIONS, values, Schema.COLUMN_PACKID
				+ "='" + packName + "'", null) != 1) {
			values.put(Schema.COLUMN_PACKID, packName);
			database.insert(Schema.TABLE_ANOTATIONS, null, values);
		}
	}

	public void putTags(String packName, String taglist) {
		tags.put(packName,taglist);
		ContentValues values = new ContentValues();
		values.put(Schema.COLUMN_TAGS,taglist);
		if (database.update(Schema.TABLE_ANOTATIONS, values, Schema.COLUMN_PACKID
				+ "='" + packName + "'", null) != 1) {
			values.put(Schema.COLUMN_PACKID, packName);
			database.insert(Schema.TABLE_ANOTATIONS, null, values);
		}
	}
}
