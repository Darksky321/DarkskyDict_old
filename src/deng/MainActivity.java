package deng;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import deng.darkskydict.R;
import deng.util.YoudaoDict;

public class MainActivity extends Activity {

	private static final int FINISHED = 1;
	private static EditText editText;
	private static ImageButton button;
	private static TextView textView;
	private static ImageButton cross;
	private Thread queryThread;
	private Runnable queryRunnable;
	private AlertDialog aboutDialog;
	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			switch (msg.what) {
			case FINISHED:
				try {
					textView.setText(YoudaoDict.handleJson(new JSONObject(
							(String) msg.obj)));
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					textView.setText((String) msg.obj);
					textView.append("\n");
					textView.append(e.toString());
				}
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.editText);
		button = (ImageButton) findViewById(R.id.button);
		textView = (TextView) findViewById(R.id.textView);
		cross = (ImageButton) findViewById(R.id.cross);

		queryRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				String ret = YoudaoDict.lookUpAWord(editText.getText()
						.toString().trim());
				Message msg = handler.obtainMessage();
				msg.what = FINISHED;
				msg.obj = ret;
				handler.sendMessage(msg);
			}
		};

		/**
		 * 输入法中按发送键登陆
		 */
		editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO 自动生成的方法存根
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					button.performClick();
					break;
				}
				return true;
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}
				if (queryThread == null || !queryThread.isAlive()) {
					queryThread = new Thread(queryRunnable);
					queryThread.start();
				} else {
					Toast.makeText(getApplicationContext(), "Querying...",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO 自动生成的方法存根
				aboutDialog = new AlertDialog.Builder(MainActivity.this)
						.create();
				aboutDialog.show();
				aboutDialog.getWindow().setContentView(R.layout.dialog_about);
				aboutDialog.getWindow().findViewById(R.id.button_ok)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								aboutDialog.dismiss();
							}
						});
				return true;
			}
		});

		textView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO 自动生成的方法存根
				ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(textView.getText().toString());
				Toast.makeText(getApplicationContext(), "Copy to clipboard.",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});

		cross.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				editText.setText("");
			}
		});
	}

}
