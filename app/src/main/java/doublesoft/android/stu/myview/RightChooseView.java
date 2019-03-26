package doublesoft.android.stu.myview;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

public class RightChooseView extends RelativeLayout {
	public boolean isShowing = false;
	private Context mContext;
	RightChooseViewAdapter adapter = null;
	RightChooseViewListener rightChooseViewListener = null;

	public RightChooseViewListener getrightChooseViewListener() {
		return rightChooseViewListener;
	}

	public void setrightChooseViewListener(
			RightChooseViewListener rightChooseViewListener) {
		this.rightChooseViewListener = rightChooseViewListener;
	}

	public RightChooseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RightChooseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public RightChooseView(Context context) {
		super(context);
		init(context);
	}

	// 初始化
	public void init(Context context) {
		mContext = context;
	}

	// 读取数据
	public void reloadData() {
		if (rightChooseViewListener != null) {
			try {
				List<ContentValues> list = rightChooseViewListener
						.arrForParentTableView();
				ListView listView = (ListView) this
						.findViewById(doublesoft.android.stu.R.id.RightChooseListView);

				if (adapter == null) {
					adapter = new RightChooseViewAdapter(mContext, list);
					listView.setAdapter(adapter);

					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {

							if (arg2 != adapter.selectItem) {
								adapter.setSelectItem(arg2);
								adapter.notifyDataSetChanged();
							}

							ContentValues contentValues = adapter
									.getItem(arg2);
							rightChooseViewListener
									.rightChooseViewDidSelectID(contentValues
											.getAsInteger("ID").intValue());

						}
					});

				} else {
					adapter.setList(list);
					adapter.notifyDataSetChanged();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	// 显示右侧菜单
	public void showRightChooseView() {
		View rightChooseBgView = this
				.findViewById(doublesoft.android.stu.R.id.RightChooseBgView);
		rightChooseBgView.getBackground().setAlpha(128);
		rightChooseBgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hiddenRightChooseView();
			}
		});

		View rightChooseContentView = this
				.findViewById(doublesoft.android.stu.R.id.RightChooseContentView);
		if (!isShowing) {
			if (adapter==null) {
				this.reloadData();
			}
			this.setVisibility(View.VISIBLE);
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			alphaAnimation.setDuration(300);
			rightChooseBgView.startAnimation(alphaAnimation);

			TranslateAnimation translateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			translateAnimation.setDuration(300);
			translateAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					if (rightChooseViewListener != null) {
						rightChooseViewListener.rightChooseViewDidShow();
					}
				}
			});
			rightChooseContentView.startAnimation(translateAnimation);

			isShowing = true;
		}
	}

	// 隐藏右侧菜单
	public void hiddenRightChooseView() {
		final View thisView = this;
		final View rightChooseBgView = this
				.findViewById(doublesoft.android.stu.R.id.RightChooseBgView);
		final View rightChooseContentView = this
				.findViewById(doublesoft.android.stu.R.id.RightChooseContentView);
		if (isShowing) {
			this.setVisibility(View.VISIBLE);
			AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			alphaAnimation.setDuration(300);
			rightChooseBgView.startAnimation(alphaAnimation);

			TranslateAnimation translateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			translateAnimation.setDuration(300);
			translateAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					rightChooseBgView.clearAnimation();
					rightChooseContentView.clearAnimation();
					thisView.setVisibility(View.GONE);

					if (rightChooseViewListener != null) {
						rightChooseViewListener.rightChooseViewDidHidden();
					}
				}
			});
			rightChooseContentView.startAnimation(translateAnimation);

			isShowing = false;
		}
	}
	
	//强制关闭，不使用动画
	public void forceHidden()
	{
		final View thisView = this;
		final View rightChooseBgView = this
				.findViewById(doublesoft.android.stu.R.id.RightChooseBgView);
		final View rightChooseContentView = this
				.findViewById(doublesoft.android.stu.R.id.RightChooseContentView);
		
		rightChooseBgView.clearAnimation();
		rightChooseContentView.clearAnimation();
		thisView.setVisibility(View.GONE);
		
		isShowing = false;
	}

	// 选中默认行
	public void selectItemByID(int ID) {
		try {
			if (adapter != null) {
				if (ID==-1) {
					adapter.setSelectItem(-1);
					adapter.notifyDataSetChanged();
				}
				
				for (int i = 0; i < adapter.getCount(); i++) {
					ContentValues contentValues = adapter
							.getItem(i);
					if (contentValues.getAsInteger("ID").intValue() == ID) {
						adapter.setSelectItem(i);
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 获取选择的ID
	public int getSelectedID() {
		try {
			if (adapter != null) {
				if (adapter.selectItem != -1) {
					ContentValues contentValues = adapter
							.getItem(adapter.selectItem);
					return contentValues.getAsInteger("ID").intValue();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return -1;
	}
}
