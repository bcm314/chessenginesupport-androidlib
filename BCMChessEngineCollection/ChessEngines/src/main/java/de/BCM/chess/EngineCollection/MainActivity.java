package de.BCM.chess.EngineCollection;

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

		setContentView(layout);
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

		b.append(getText(R.string.to_use_them));
		b.append("\n\n");

		b.append(getText(R.string.includes_the_following_engines));
		b.append("\n\n");

		appendDetail(b, "OpenTal", "1.1", "http://www.pkoziol.cal24.pl/opental");
		appendDetail(b, "Toga II", "4.01", "http://www.mediafire.com/file/xeukohvobjobbv4/TogaII401.zip");
		appendDetail(b, "Gambit Fruit", "2.2 beta 4bx", "https://github.com/lazydroid/gambit-fruit");
		appendDetail(b, "Laser", "1.8 beta", "https://github.com/jeffreyan11/laser-chess-engine");
		appendDetail(b, "Andscacs", "0.921", "https://github.com/MichaelB7/Andscacs");
		appendDetail(b, "CT800", "1.40", "https://www.ct800.net");
		appendDetail(b, "Ethereal", "12.08", "https://github.com/AndyGrant/Ethereal");
		appendDetail(b, "Senpai", "2.0", "https://github.com/MichaelB7/Senpai");
		appendDetail(b, "SugaR XPrO", "20200420", "https://github.com/MichaelB7/SugaR");
		appendDetail(b, "Stockfish", "20200409", "https://github.com/official-stockfish");
		appendDetail(b, "Demolito", "20200424", "https://github.com/lucasart/Demolito");
		appendDetail(b, "Kōhai", "1.0", "https://github.com/MichaelB7/Kohai-Chess");
		appendDetail(b, "RubiChess", "1.8", "https://github.com/Matthies/RubiChess");
		appendDetail(b, "Sungorus", "1.4", "https://sites.google.com/site/sungorus");
		appendDetail(b, "Nayeem", "10.1", "https://github.com/MohamedNayeem/Nayeem");
		appendDetail(b, "Texel", "1.08a17", "https://github.com/peterosterlund2/texel");

		return b;
	}

	private void appendDetail(SpannableStringBuilder b, String title, String subtitle, final String url) {
		b.append("    ");

		int nameStart = b.length();
		b.append(title);
		b.setSpan(new RelativeSizeSpan(1.2f), nameStart, b.length(), 0);
		b.setSpan(new StyleSpan(Typeface.BOLD), nameStart, b.length(), 0);
		b.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				launchUri(url);
			}
		}, nameStart, b.length(), 0);

		b.append(" ");

		int subtitleStart = b.length();
		b.append(subtitle);

//		b.setSpan(new RelativeSizeSpan(0.8f), subtitleStart, b.length(), 0);
//		b.setSpan(new ForegroundColorSpan(0xff808080), subtitleStart, b.length(), 0);

/*
		if (!url.equals("")) {
			b.append("\n    ");

			b.append("(");

			int downloadLinkStart = b.length();
			b.append("source code");
			b.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					launchUri(url);
				}
			}, downloadLinkStart, b.length(), 0);

			b.append(")");
			b.append("\n");
		}
*/
		b.append("\n");
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
}
