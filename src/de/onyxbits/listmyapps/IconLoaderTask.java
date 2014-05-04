package de.onyxbits.listmyapps;

import java.lang.ref.WeakReference;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

/**
 * Helper for loading images without blocking the UI thread.
 * 
 * @author patrick
 * 
 */
class IconLoaderTask extends AsyncTask<ApplicationInfo, Void, Drawable> {
	private final WeakReference<ImageView> imageViewReference;
	

	private PackageManager packageManager;
	
	public IconLoaderTask(PackageManager pm, ImageView imageView) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.packageManager = pm;
	}

	// Decode image in background.
	@Override
	protected Drawable doInBackground(ApplicationInfo... params) {
		return params[0].loadIcon(packageManager);
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Drawable drawable) {
		if (imageViewReference != null) {
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {

				if (drawable != null) {
					imageView.setImageDrawable(drawable);
				}
			}
		}
	}
}