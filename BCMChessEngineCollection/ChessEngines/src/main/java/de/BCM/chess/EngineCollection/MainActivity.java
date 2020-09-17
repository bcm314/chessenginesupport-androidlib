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

		appendDetail(b, "Andscacs", "0.921", "https://github.com/MichaelB7/Andscacs");
		appendDetail(b, "Cheng", "4.40", "https://github.com/kmar/cheng4");
		appendDetail(b, "Cheng", "4.40", "https://github.com/kmar/cheng4");
		appendDetail(b, "Cinnamon", "2.2a", "https://github.com/gekomad/Cinnamon");
		appendDetail(b, "Demolito", "20200908", "https://github.com/lucasart/Demolito");
		appendDetail(b, "Ethereal", "12.08", "https://github.com/AndyGrant/Ethereal");
		appendDetail(b, "Gambit Fruit", "2.2 beta 4bx", "https://github.com/lazydroid/gambit-fruit");
		appendDetail(b, "Kōhai", "1.0", "https://github.com/MichaelB7/Kohai-Chess");
		appendDetail(b, "Laser", "1.8 beta", "https://github.com/jeffreyan11/laser-chess-engine");
		appendDetail(b, "Nayeem", "10.1", "https://github.com/MohamedNayeem/Nayeem");
		appendDetail(b, "OpenTal", "1.1", "http://www.pkoziol.cal24.pl/opental");
		appendDetail(b, "Rodent IV", "0.32", "https://github.com/nescitus/rodent-iv");
		appendDetail(b, "RubiChess", "1.8", "https://github.com/Matthies/RubiChess");
		appendDetail(b, "Senpai", "2.0", "https://github.com/MichaelB7/Senpai");
		appendDetail(b, "Sting", "11.2", "http://www.talkchess.com/forum3/download/file.php?id=170");
		appendDetail(b, "Stockfish", "20200917", "https://github.com/official-stockfish");
		// appendDetail(b, "Stockfish NNUE", "20200908", "https://github.com/joergoster/Stockfish-NNUE");
		appendDetail(b, "SugaR XPrO", "20200908", "https://github.com/MichaelB7/SugaR");
		appendDetail(b, "Sungorus", "1.4", "https://sites.google.com/site/sungorus");
		appendDetail(b, "Texel", "1.08a17", "https://github.com/peterosterlund2/texel");
		appendDetail(b, "Toga II", "4.01", "http://www.mediafire.com/file/xeukohvobjobbv4/TogaII401.zip");

		b.append("\nAdditional engines built by others:\n");
		b.append("You have to include it yourself, because I didn't checked redistribution permissions!\n\n");
		appendDetail(b, "DanaSah", "7.9", "https://github.com/pmcastro/danasah790");
		appendDetail(b, "Aristides", "20171222", "https://play.google.com/store/apps/details?id=app.packs.chessenginesotherfish64pack");
		appendDetail(b, "BrainFish", "20180423", "https://play.google.com/store/apps/details?id=app.packs.chessenginesotherfish64pack");
		appendDetail(b, "Cfish", "20180418", "https://play.google.com/store/apps/details?id=app.packs.chessenginesotherfish64pack");
		appendDetail(b, "CiChess", "20171107", "https://play.google.com/store/apps/details?id=app.packs.chessenginesotherfish64pack");
		appendDetail(b, "Corchess", "20180425", "https://play.google.com/store/apps/details?id=app.packs.chessenginesotherfish64pack");
		appendDetail(b, "McBrain", "20180210", "https://play.google.com/store/apps/details?id=app.packs.chessenginesotherfish64pack");
		appendDetail(b, "BikJump", "2.5", "https://play.google.com/store/apps/details?id=com.eng.engines");
		appendDetail(b, "Komodo", "9", "https://play.google.com/store/apps/details?id=com.komodochess.komodo9");
		appendDetail(b, "Cheese", "2.1", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "Bluefish-NN", "200320", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "Godel", "7.0", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "GoldOno", "7.0", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "HekaSF", "0.95", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "Honey", "X5i", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "Igel", "2.3.1", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "MoonFish", "270220", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "Pedone", "2.0", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "ShashChess", "8.0", "https://chessengines.blogspot.com/p/download.html");
		appendDetail(b, "Gogobello", "2.2", "http://sasachess.altervista.org/gogobello/index.html");
		appendDetail(b, "Gull", "3", "http://chesstroid.blogspot.com/2017/04/android-uci-engine-update-gull-3ap-with.html");
		appendDetail(b, "asmFish", "20181204", "https://play.google.com/store/apps/details?id=com.acidapestudios.chessenginescollection");
		appendDetail(b, "Hakkapeliitta", "3.0", "https://play.google.com/store/apps/details?id=com.acidapestudios.chessenginescollection");
		appendDetail(b, "Arasan 20170209", "(32bit only)", "https://play.google.com/store/apps/details?id=app.packs.activechessenginespack");
		appendDetail(b, "Gnu Chess", "20161029 (32bit only)", "https://play.google.com/store/apps/details?id=app.packs.activechessenginespack");
		appendDetail(b, "Greko", "20161231 (32bit only)", "https://play.google.com/store/apps/details?id=app.packs.activechessenginespack");
		appendDetail(b, "Rodent II risky", "20170205 (32bit only)", "https://play.google.com/store/apps/details?id=app.packs.activechessenginespack");

		return b;
	}

	private void appendDetail(SpannableStringBuilder b, String title, String subtitle, final String url) {
		b.append("    ");

		int nameStart = b.length();
		b.append(title);
		if (!url.equals("")) {
			b.setSpan(new RelativeSizeSpan(1.2f), nameStart, b.length(), 0);
			b.setSpan(new StyleSpan(Typeface.BOLD), nameStart, b.length(), 0);
			b.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					launchUri(url);
				}
			}, nameStart, b.length(), 0);
		}
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
