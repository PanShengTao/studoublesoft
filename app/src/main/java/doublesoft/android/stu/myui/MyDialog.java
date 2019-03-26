package doublesoft.android.stu.myui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MyDialog extends Dialog implements DialogInterface {
	public MyDialog(Context context, int theme) {
		super(context, theme);
	}

	public MyDialog(Context context) {
		super(context);
	}

	public static class Builder {
		public final LayoutInflater mInflater;
		private Context context;
		private String title;
		private String message;

		private CharSequence[] items;
		private ListView listView;
		private ListAdapter adapter;
		private DialogInterface.OnClickListener itemClickListener;

		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;

		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		// 对话框标题
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		// 消息内容
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		// 对话框视图
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		// 确定按钮
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		// 取消按钮
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		// 设置列表
		public Builder setItems(int itemsId, final OnClickListener listener) {
			this.items = context.getResources().getTextArray(itemsId);
			this.itemClickListener = listener;
			return this;
		}

		public Builder setItems(CharSequence[] items,
				final OnClickListener listener) {
			this.items = items;
			this.itemClickListener = listener;
			return this;
		}

		/**
		 * Create the dialog
		 */
		public MyDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final MyDialog dialog = new MyDialog(context, doublesoft.android.stu.R.style.MyDialog);
			dialog.setCanceledOnTouchOutside(false);
			View layout = inflater.inflate(doublesoft.android.stu.R.layout.public_mydialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			// set the dialog title
			LinearLayout myDialogTitleLinearLayout = (LinearLayout) layout
					.findViewById(doublesoft.android.stu.R.id.MyDialogTitleLinearLayout);
			if (title != null) {
				((TextView) layout.findViewById(doublesoft.android.stu.R.id.MyDialogTitle))
						.setText(title);
			} else {
				myDialogTitleLinearLayout.setVisibility(View.GONE);
			}

			// set the confirm button
			Button positiveButton = (Button) layout
					.findViewById(doublesoft.android.stu.R.id.MyDialogPositiveButton);
			if (positiveButtonText != null) {
				positiveButton.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					positiveButton
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}

				if (negativeButtonText == null) {
					LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) positiveButton
							.getLayoutParams();
					layoutParams.setMargins(0, 0, 0, 0);
					positiveButton.setLayoutParams(layoutParams);
				}
			} else {
				// if no confirm button just set the visibility to GONE
				positiveButton.setVisibility(View.GONE);
			}

			// set the cancel button
			Button negativeButton = (Button) layout
					.findViewById(doublesoft.android.stu.R.id.MyDialogNegativeButton);
			if (negativeButtonText != null) {
				negativeButton.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					negativeButton
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				negativeButton.setVisibility(View.GONE);
			}

			// set the content message
			LinearLayout myDialogContentLinearLayout = (LinearLayout) layout
					.findViewById(doublesoft.android.stu.R.id.MyDialogContentLinearLayout);

			if (message != null) {
				((TextView) layout.findViewById(doublesoft.android.stu.R.id.MyDialogMessage))
						.setText(message);
			} else {
				myDialogContentLinearLayout.removeAllViews();
				if (contentView != null) {// 自定义视图
					myDialogContentLinearLayout.addView(contentView,
							new LayoutParams(LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
				} else if (items != null) {// 列表
					listView = (ListView) mInflater.inflate(
							doublesoft.android.stu.R.layout.public_listview, null);
					adapter = new ArrayAdapter<CharSequence>(context,
							doublesoft.android.stu.R.layout.public_mydialog_item, android.R.id.text1,
							items);
					listView.setAdapter(adapter);
					if (this.itemClickListener != null) {
						listView.setOnItemClickListener(new OnItemClickListener() {
							@SuppressWarnings("rawtypes")
							public void onItemClick(AdapterView parent, View v,
                                                    int position, long id) {
								itemClickListener.onClick(dialog, position);
							}
						});
					}
					myDialogContentLinearLayout.setPadding(0, 0, 0, 0);
					myDialogContentLinearLayout.setMinimumHeight(0);
					myDialogContentLinearLayout.addView(listView,
							new LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT));
				}
			}

			dialog.setContentView(layout);

			return dialog;
		}
	}
}
