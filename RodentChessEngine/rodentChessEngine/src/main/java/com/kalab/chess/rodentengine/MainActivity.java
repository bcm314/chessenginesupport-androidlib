package com.kalab.chess.rodentengine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.style.StyleSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class MainActivity extends Activity {

	private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
	private boolean copyErrorB;
	private boolean skipExistingB = true;
	private String copyPath = "";
	private Button but1;

	private class HeaderView extends LinearLayout {
		public HeaderView(Context context) {
			super(context);

			ImageView icon = new ImageView(context);
			icon.setImageResource(R.drawable.ic_launcher);
			icon.setPadding(10, 10, 10, 10);
			int iconSize = getRawPixels(48f);
			LayoutParams iconParams = LinearLayoutParams();
			iconParams.width = iconSize;
			iconParams.height = iconSize;
			iconParams.gravity = Gravity.CENTER_VERTICAL;
			addView(icon, iconParams);

			TextView text = new TextView(context);
			text.setGravity(Gravity.CENTER);
			text.setText(R.string.app_name);
			LayoutParams textParams = LinearLayoutParams();
			textParams.gravity = Gravity.CENTER_VERTICAL;
			addView(text, textParams);
		}
	}

	private class VerticalSeparator extends View {
		private final int col = 0xff808080;
		public VerticalSeparator(Context context) {
			super(context, null);
			setBackgroundColor(col);
		}
		public VerticalSeparator(Context context, AttributeSet attrs) {
			super(context, attrs);
			setBackgroundColor(col);
		}
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getRawPixels(1f), MeasureSpec.EXACTLY));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		int spacing = getRawPixels(12f);
		layout.setPadding(spacing, spacing, spacing, spacing);

		HeaderView header = new HeaderView(this);
		LinearLayout.LayoutParams headerParams = LinearLayoutParams();
		headerParams.gravity = Gravity.CENTER_HORIZONTAL;
		layout.addView(header, headerParams);

		layout.addView(new VerticalSeparator(this));

		TextView label = new TextView(this);
		label.setMovementMethod(LinkMovementMethod.getInstance());
		label.setText(getLabel());
		LinearLayout.LayoutParams labelParams = LinearLayoutParams();
		labelParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
		labelParams.weight = 1.0f;
		labelParams.topMargin = spacing;
		layout.addView(label, labelParams);

		but1 = new Button(this);
		but1.setText("copy book and personality files to /sdcard/Rodent4");
		but1.setPadding(20, 20, 20, 20);
		LinearLayout.LayoutParams but1Params = LinearLayoutParams();
		but1Params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		but1Params.weight = 0f;
		// but1Params.setMargins(10, 30, 10, 10);
		but1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				skipExistingB = false;
				copyFileOrDir("Rodent4");
			}});
		layout.addView(but1, but1Params);

		setContentView(layout);

		copyFileOrDir("Rodent4/personalities");
	}

	String getVersionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return "undefined";
		}
	}

	private CharSequence getLabel()  {
		SpannableStringBuilder b = new SpannableStringBuilder();

		b.append(getText(R.string.includes_the_following_engines));
		b.append("\n\n");

		appendDetail(b, "Chess Engine Version", getVersionName());
		b.append("\n\n");

		b.append(getText(R.string.to_use_them));
		b.append("\n\n");

		b.append(getText(R.string.app_name));
		b.append(" ");
		b.append(getText(R.string.released_under));

		b.append(" (");

		int downloadLinkStart = b.length();

		b.append("source code");
		b.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				launchUri(getText(R.string.source_code));
			}
		}, downloadLinkStart, b.length(), 0);

		b.append(").");

		b.append("\n\n");
		b.append("There are also the following engines included\n");
		appendDetail(b, "OpenTal", "V1.1");
		b.append("\n\n");

		return b;
	}

	private void appendDetail(SpannableStringBuilder b, String title, String subtitle) {
		b.append("    ");

		int nameStart = b.length();
		b.append(title);
		b.setSpan(new StyleSpan(Typeface.BOLD), nameStart, b.length(), 0);

		b.append("\n    ");

		int subtitleStart = b.length();
		b.append(subtitle);

		b.setSpan(new RelativeSizeSpan(0.8f), subtitleStart, b.length(), 0);
		b.setSpan(new ForegroundColorSpan(0xff808080), subtitleStart, b.length(), 0);
	}

	void launchUri(CharSequence uri) {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString())));
		} catch (Exception e) {
			//Log.w(LOG_TAG, "cannot display website: $e");
		}
	}

	private int getRawPixels(float dp) {
		return (int)(dp * getResources().getDisplayMetrics().density + 0.5f);
	}

	private LinearLayout.LayoutParams LinearLayoutParams() {
		return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	private void copyFileOrDir(String path) {

		if (!HavePermission()) {
			RequestPermission();
			// this sub must be called again after got permission
			copyPath = path;
			return;
		}

		copyErrorB = false;
		AssetManager assetManager = this.getAssets();
		String assets[] = null;
		try {
			assets = assetManager.list(path);
			if (assets.length == 0) {
				copyFile(path);
			} else {
				String fullPath = "/sdcard/" + path;
				File dir = new File(fullPath);
				if (!dir.exists())
					dir.mkdir();
				for (int i = 0; i < assets.length; ++i) {
					String fileorDirName = path + "/" + assets[i];
					copyFileOrDir(fileorDirName);
					if (copyErrorB) {
						Toast.makeText(getApplicationContext(),"Error writing " + fileorDirName,
							Toast.LENGTH_LONG).show();
						return; // stop after the first write-error
					}
				}
			}
		} catch (IOException ex) {
			Log.e("tag", "I/O Exception", ex);
		}
	}

	private void copyFile(String filename) throws IOException {
		AssetManager assetManager = this.getAssets();

		String newFileName = "/sdcard/" + filename;

		File file = new File(newFileName);
		if(file.exists() && skipExistingB) {
			// Toast.makeText(getApplicationContext(),"Already exist " + newFileName, Toast.LENGTH_SHORT).show();
			return;
		}

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			if (newFileName.substring(newFileName.length()-4).equals(".bin"))
				Toast.makeText(getApplicationContext(),"Written " + newFileName, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
			copyErrorB = true;
			throw e;
		}
	}

	private boolean HavePermission() {
		return (ContextCompat.checkSelfPermission (getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED &&
			ContextCompat.checkSelfPermission (getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED);
	}

	private void RequestPermission() {

		// Check whether permission to Read and Write to External Storage is grated or not..
		if (!HavePermission()) {
			// request permission at runtime.
			ActivityCompat.requestPermissions (MainActivity.this,
				new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
				MY_PERMISSIONS_REQUEST_READ_CONTACTS);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Toast.makeText(getApplicationContext(),"permission was granted", Toast.LENGTH_LONG).show();
					if (!copyPath.equals(""))
						copyFileOrDir(copyPath);
				} else {
					// Toast.makeText(getApplicationContext(),"permission denied,", Toast.LENGTH_LONG).show();
					but1.setVisibility(View.GONE);
				}
				return;
			}
		}
	}
}
