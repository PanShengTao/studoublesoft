package doublesoft.android.stu.myview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;

import java.util.ArrayList;
import java.util.List;

//
//贵州双软科技有限公司
//版本 1.2 增加参数
//Updated by yy on 18-12-24.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class ButtonColumn extends RelativeLayout {
    public MyFunction myFunc;
    public MyHandler myHandler;
    public Context myContext = null;
    public ButtonColumnListener buttonColumnListener = null;

    // 节点按钮数组
    public List<Button> itemBtnArr = null;

    // 节点标题数组
    public List<String> itemTitleArr = null;

    // 节点宽度高度，如果不指定则根据title自适应
    public float itemWidth = 0;
    public float itemHeight = 0;

    // 节点与节点的间隙 与滚动方向对应 水平滚动时设置垂直间隙无效，反之亦然
    public int itemHorizontalSpace = 0;
    public int itemVerticalSpace = 0;

    // 节点标题的Padding
    public int itemPaddingLeftAndRight = 0;
    public int itemPaddingTopAndBottom = 0;

    // 选中的节点索引
    public int itemSelectedIndex = 0;

    // 节点字体大小
    public float itemFontSize = 15;
    public float itemSelectedFontSize = 15;

    // 选中的按钮文字加粗
    public boolean itemSelectedFontBold = false;

    //按钮对齐方式
    public int itemGravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

    // 节点普通和选中时的图片
    public int itemBGImage;

    // 节点普通和选中时的文字颜色
    public int itemFontColor;

    // 滚动方向 0水平方向 1垂直方向
    public int itemDirection = 0;

    // 是否保持选中的按钮居中
    public boolean itemKeepCenter = false;

    // 用户标签
    public String userTag = "";

    // 是否显示滑动选中效果
    public boolean itemMoveEffect = false;
    public int itemMoveImage;

    public ButtonColumn(Context context) {
        super(context);
        setDefaultVars(context);
    }

    public ButtonColumn(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultVars(context);
    }

    public ButtonColumn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaultVars(context);
    }

    public ButtonColumnListener getButtonColumnListener() {
        return buttonColumnListener;
    }

    public void setButtonColumnListener(ButtonColumnListener buttonColumnListener) {
        this.buttonColumnListener = buttonColumnListener;
    }

    // 初始化
    public void setDefaultVars(Context context) {
        myContext = context;
        myFunc = new MyFunction();
        myHandler = new MyHandler();

        itemBtnArr = new ArrayList<Button>();
        itemBGImage = doublesoft.android.stu.R.drawable.public_trans;
        itemMoveImage = doublesoft.android.stu.R.drawable.public_trans;
        itemFontColor = doublesoft.android.stu.R.color.selector_btn_darkgray_white;
    }

    // 读取数据
    public void reloadData() {
        try {
            if (this.itemTitleArr == null || this.itemTitleArr.size() == 0) {
                return;
            }

            final View moveBtn = (View) this.findViewById(doublesoft.android.stu.R.id.ButtonColumnMoveBtn);
            final LinearLayout linearLayout = (LinearLayout) this.findViewById(doublesoft.android.stu.R.id.ButtonColumnLinearLayout);

            // 设置大小
            moveBtn.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));

            // 移除全部子视图
            linearLayout.removeAllViews();
            itemBtnArr.clear();

            // 必须放在这个位置，否则Padding无效
            if (itemMoveEffect == false) {
                moveBtn.setVisibility(View.GONE);
            } else {
                moveBtn.setVisibility(View.VISIBLE);
                moveBtn.setBackgroundResource(this.itemMoveImage);
            }

            // 创建按钮
            for (int i = 0; i < this.itemTitleArr.size(); i++) {
                final Button itemBtn = new Button(myContext);

                itemBtn.setText(itemTitleArr.get(i));
                itemBtn.setTextSize(this.itemFontSize);
                itemBtn.setGravity(this.itemGravity);
                itemBtn.setTextColor((ColorStateList) getResources().getColorStateList(this.itemFontColor));

                // 设置宽度高度
                itemBtn.setLayoutParams(new LinearLayout.LayoutParams(
                        this.itemWidth != 0
                                ? myFunc.dip2px(myContext, this.itemWidth + this.itemPaddingLeftAndRight * 2)
                                : android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        this.itemHeight != 0
                                ? myFunc.dip2px(myContext, this.itemHeight + this.itemPaddingTopAndBottom * 2)
                                : android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

                // 设置Padding
                itemBtn.setPadding(myFunc.dip2px(myContext, this.itemPaddingLeftAndRight),
                        myFunc.dip2px(myContext, this.itemPaddingTopAndBottom),
                        myFunc.dip2px(myContext, this.itemPaddingLeftAndRight),
                        myFunc.dip2px(myContext, this.itemPaddingTopAndBottom));

                //背景图
                if (itemMoveEffect == true && this.itemSelectedIndex == i) {
                    itemBtn.setBackgroundResource(this.itemMoveImage);
                } else {
                    itemBtn.setBackgroundResource(this.itemBGImage);
                }

                // 选中
                if (this.itemSelectedIndex == i) {
                    setItemBtn(itemBtn, true);

                    if (itemMoveEffect == true) {
                        myHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                        itemBtn.getMeasuredWidth(), itemBtn.getMeasuredHeight());
                                if (itemDirection == 0) {
                                    layoutParams.setMargins(itemBtn.getLeft(), 0, 0, 0);
                                } else {
                                    layoutParams.setMargins(0, itemBtn.getTop(), 0, 0);
                                }
                                moveBtn.setLayoutParams(layoutParams);
                                itemBtn.setBackgroundResource(itemBGImage);
                            }
                        }, 1000);
                    }
                }

                itemBtn.setTag(i);

                // 设置按钮间间距
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(itemBtn.getLayoutParams());
                // 水平滚动方向
                if (itemDirection == 0) {
                    if (i > 0) {
                        lp.setMargins(myFunc.dip2px(myContext, this.itemHorizontalSpace), 0, 0, 0);
                    }
                } else if (itemDirection == 1) {
                    // 垂直滚动方向
                    if (i > 0) {
                        lp.setMargins(0, myFunc.dip2px(myContext, this.itemVerticalSpace), 0, 0);
                    }
                }
                itemBtn.setLayoutParams(lp);

                // 点击事件
                itemBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        itemClick(Integer.parseInt(arg0.getTag().toString()));
                    }
                });

                itemBtnArr.add(itemBtn);
                linearLayout.addView(itemBtn);

                // 调整位置
                justScrollPosition(this.itemSelectedIndex);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // 点击事件
    public void itemClick(final int itemIndex) {
        // 设置选中
        this.selectItem(itemIndex);
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                notifyClick(itemIndex);
            }
        }, 160);
    }

    // 通知
    public void notifyClick(int itemIndex) {
        if (this.buttonColumnListener != null) {
            this.buttonColumnListener.itemClick(itemIndex);
        }
    }

    // 选中指定节点
    public void selectItem(final int itemIndex) {
        final View moveBtn = (View) this.findViewById(doublesoft.android.stu.R.id.ButtonColumnMoveBtn);
        if (itemIndex >= 0 && itemIndex < this.itemTitleArr.size()) {
            // 状态
            for (int i = 0; i < this.itemBtnArr.size(); i++) {
                final Button itemBtn = itemBtnArr.get(i);
                if (Integer.parseInt(itemBtn.getTag().toString()) == itemIndex) {
                    // 调整位置
                    justScrollPosition(itemIndex);
                    this.itemSelectedIndex = itemIndex;
                    setItemBtn(itemBtn, true);

                    // 滑动滑块
                    if (itemMoveEffect) {
                        AnimationSet animationSet = new AnimationSet(true);

                        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,
                                (float) (itemBtn.getMeasuredWidth() * 1.0 / moveBtn.getMeasuredWidth()), 1.0f,
                                (float) (itemBtn.getMeasuredHeight() * 1.0 / moveBtn.getMeasuredHeight()));

                        TranslateAnimation translateAnimation = null;

                        if (itemDirection == 0) {
                            translateAnimation = new TranslateAnimation(0, itemBtn.getLeft() - moveBtn.getLeft(), 0f,
                                    0f);
                        } else {
                            translateAnimation = new TranslateAnimation(0, 0, 0f, itemBtn.getTop() - moveBtn.getTop());
                        }

                        animationSet.addAnimation(scaleAnimation);
                        animationSet.addAnimation(translateAnimation);
                        animationSet.setFillAfter(false);
                        animationSet.setDuration(150);
                        animationSet.setAnimationListener(new AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                        itemBtn.getMeasuredWidth(), itemBtn.getMeasuredHeight());
                                if (itemDirection == 0) {
                                    layoutParams.setMargins(itemBtn.getLeft(), 0, 0, 0);
                                } else {
                                    layoutParams.setMargins(0, itemBtn.getTop(), 0, 0);
                                }
                                moveBtn.setLayoutParams(layoutParams);
                                moveBtn.clearAnimation();
                            }
                        });
                        moveBtn.startAnimation(animationSet);
                    }
                } else {
                    setItemBtn(itemBtn, false);
                }
            }
        }
    }

    // 设置状态 及 加粗
    public void setItemBtn(Button itemBtn, boolean selected) {
        itemBtn.setSelected(selected);

        //字号
        if (selected) {
            itemBtn.setTextSize(itemSelectedFontSize);
        } else {
            itemBtn.setTextSize(itemFontSize);
        }

        //加粗
        if (itemSelectedFontBold) {
            if (selected) {
                itemBtn.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                itemBtn.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    // 调整滚动位置
    public void justScrollPosition(int itemIndex) {
        try {
            if (itemIndex >= 0 && itemIndex < this.itemTitleArr.size()) {
                for (int i = 0; i < this.itemBtnArr.size(); i++) {
                    final Button itemBtn = itemBtnArr.get(i);
                    if (Integer.parseInt(itemBtn.getTag().toString()) == itemIndex) {
                        View buttonColumnScrollView = this.findViewById(doublesoft.android.stu.R.id.ButtonColumnScrollView);
                        final LinearLayout linearLayout = (LinearLayout) this
                                .findViewById(doublesoft.android.stu.R.id.ButtonColumnLinearLayout);
                        if (buttonColumnScrollView != null) {
                            if (itemDirection == 0
                                    && buttonColumnScrollView.getClass().equals(HorizontalScrollView.class)) {// 水平方向
                                if (linearLayout.getMeasuredWidth() > buttonColumnScrollView.getMeasuredWidth()) {
                                    HorizontalScrollView scrollView = (HorizontalScrollView) buttonColumnScrollView;
                                    int centerX = (itemBtn.getLeft() + itemBtn.getRight()) / 2;
                                    if (centerX < scrollView.getMeasuredWidth() / 2) {// 前半部分滚动到起始位置
                                        scrollView.smoothScrollTo(0, scrollView.getScrollY());
                                    } else if (centerX > linearLayout.getMeasuredWidth()
                                            - scrollView.getMeasuredWidth() / 2) {// 后半部分滚动到结束位置
                                        scrollView.smoothScrollTo(
                                                linearLayout.getMeasuredWidth() - scrollView.getMeasuredWidth(),
                                                scrollView.getScrollY());
                                    } else {
                                        if (itemKeepCenter) {// 滚动到居中位置
                                            scrollView.smoothScrollTo(centerX - scrollView.getMeasuredWidth() / 2,
                                                    scrollView.getScrollY());
                                        }
                                    }
                                }
                            } else if (itemDirection == 1
                                    && buttonColumnScrollView.getClass().equals(ScrollView.class)) {// 垂直方向
                                if (linearLayout.getMeasuredHeight() > buttonColumnScrollView.getMeasuredHeight()) {
                                    ScrollView scrollView = (ScrollView) buttonColumnScrollView;
                                    int centerY = (itemBtn.getTop() + itemBtn.getBottom()) / 2;
                                    if (centerY < scrollView.getMeasuredHeight() / 2) {// 前半部分滚动到起始位置
                                        scrollView.smoothScrollTo(scrollView.getScrollX(), 0);
                                    } else if (centerY > linearLayout.getMeasuredHeight()
                                            - scrollView.getMeasuredHeight() / 2) {// 后半部分滚动到结束位置
                                        scrollView.smoothScrollTo(scrollView.getScrollX(),
                                                linearLayout.getMeasuredHeight() - scrollView.getMeasuredHeight());
                                    } else {
                                        if (itemKeepCenter) {// 滚动到居中位置
                                            scrollView.smoothScrollTo(scrollView.getScrollX(),
                                                    centerY - scrollView.getMeasuredHeight() / 2);
                                        }
                                    }
                                }
                            }
                        }

                    }

                }
            }
        } catch (Exception e) {
        }
    }

    // 设置默认参数
    public void setDefault(int kind) {
        if (kind == 1) {// 如文章栏目选择
            this.itemHorizontalSpace = 6;
            this.itemHeight = 33.5f;
            this.itemFontSize = 15.0f;
            this.itemFontColor = doublesoft.android.stu.R.color.selector_btn_darkgray_white;
            this.itemBGImage = doublesoft.android.stu.R.drawable.public_trans;
            this.itemMoveEffect = true;
            this.itemMoveImage = doublesoft.android.stu.R.drawable.selector_btn_blue_top_r4;
        } else if (kind == 2) {// 订餐 分类
            // this.itemSpace = 32;
            // this.itemHeight = 80.0f;
            // this.itemFontSize = 36.0f;
            // this.itemFontColor = (ColorStateList) getResources()
            // .getColorStateList(R.color.selector_btn_darkgray_green);
            // this.itemBGImage = R.drawable.trans;
            // this.showMoveEffect = true;
            // this.itemMoveImage = R.drawable.line_bottom_green4;
        } else if (kind == 3) {// 垂直滚动选择 测试
            this.itemHeight = 33.5f;
            this.itemFontSize = 15.0f;
            this.itemFontColor = doublesoft.android.stu.R.color.selector_btn_darkgray_white;
            this.itemBGImage = doublesoft.android.stu.R.drawable.selector_btn_trans_blue;
            this.itemMoveEffect = true;
            this.itemMoveImage = doublesoft.android.stu.R.drawable.shape_blue;
        }
    }
}
