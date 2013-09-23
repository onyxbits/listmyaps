package de.onyxbits.listmyapps;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AnnotationsSource {

	private Schema schema;
	private SQLiteDatabase database;

	public AnnotationsSource(Context ctx) {
		schema = new Schema(ctx);
	}

	public void open() throws SQLException {
		database = schema.getWritableDatabase();
	}


}
