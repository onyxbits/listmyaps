package de.onyxbits.listmyapps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FormatsSource {

	private Schema schema;
	private SQLiteDatabase database;
	private static final String[] all = { Schema.COLUMN_ID, Schema.COLUMN_FNAME,
			Schema.COLUMN_HEADER, Schema.COLUMN_ITEM, Schema.COLUMN_FOOTER };

	public FormatsSource(Context ctx) {
		schema = new Schema(ctx);
	}

	public void open() throws SQLException {
		database = schema.getWritableDatabase();
	}

	public FormatsData get(long id) {
		Cursor cursor = database.query(Schema.TABLE_FORMATS, all, "_id=" + id,
				null, null, null, null);
		cursor.moveToFirst();
		FormatsData ret = new FormatsData();
		ret = new FormatsData();
		ret.id = cursor.getLong(0);
		ret.formatName = cursor.getString(1);
		ret.header = cursor.getString(2);
		ret.item = cursor.getString(3);
		ret.footer = cursor.getString(4);
		return ret;
	}

	public void add(FormatsData data) {
	}

	public void remove(long id) {
	}

	public List<FormatsData> list() {
		
		List<FormatsData> ret = new ArrayList<FormatsData>();
		String[] all = { Schema.COLUMN_ID, Schema.COLUMN_FNAME,
				Schema.COLUMN_HEADER, Schema.COLUMN_ITEM, Schema.COLUMN_FOOTER };
		Cursor cursor = database.query(Schema.TABLE_FORMATS, all, null, null, null,
				null, null);


		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
      FormatsData fd =new FormatsData();
      fd.id = cursor.getLong(0);
			fd.formatName = cursor.getString(1);
			fd.header = cursor.getString(2);
			fd.item = cursor.getString(3);
			fd.footer = cursor.getString(4);
      ret.add(fd);
      cursor.moveToNext();
    }
		cursor.close();
		return ret;
	}

}
