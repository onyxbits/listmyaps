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

	public AnnotationsSource(Context ctx) {
		schema = new Schema(ctx);
		comments = new HashMap<String, String>();
	}

	public void open() throws SQLException {
		database = schema.getWritableDatabase();
		cache();
	}

	public String getComment(String packName) {
		return comments.get(packName);
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

	private void cache() {
		String[] all = { Schema.COLUMN_PACKID, Schema.COLUMN_COMMENT };

		Cursor cursor = database.query(Schema.TABLE_ANOTATIONS, all, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			comments.put(cursor.getString(0), cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();
	}
}
