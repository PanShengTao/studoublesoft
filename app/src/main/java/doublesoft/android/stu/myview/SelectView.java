package doublesoft.android.stu.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import doublesoft.android.stu.R;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.myui.ImageButton;
import doublesoft.android.stu.myui.MyButton;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-21.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

@SuppressLint("InflateParams")
public class SelectView extends RelativeLayout {
    public Context myContext;
    public MyFunction myFunc = null;
    public MyHandler myHandler;
    private LayoutInflater mInflater;
    public SelectViewListener selectViewListener = null;
    public PopViewListener popViewListener = null;

    // 标题控件
    public TextView selectLabel;
    public ImageButton selectBtn;
    public View selectSplitLine;// 标题分割线

    // 节点能被选中的数量控制
    public int itemAllowSelectMinCount;// 最小数量
    public int itemAllowSelectMaxCount;// 最大数量

    // 每行排几个节点按钮
    public int itemPerRowCount;// 默认1

    // 节点数组
    public List<MyButton> itemBtnArr;// 节点按钮数组，根据用户提供的值生成按钮
    public List<String> itemTitleArr;// 节点标题数组
    public List<String> itemValueArr;// 节点值数组

    // 选中节点数组
    public List<String> itemSelectedIndexArr;

    // 高度 默认30，宽度由下面两个LabelWidth值决定
    public int selectViewWidth;// 设置了控件宽度后，后面两个值无效
    public int selectViewHeight;

    // 标题最小、最大宽度
    public int selectLabelMinWidth;
    public int selectLabelMaxWidth;

    // 节点标题 弹窗的标题
    public String selectLabelTitle;
    public String selectPopViewTitle;

    // 弹框
    public PopView selectPopView;// 由配置时传进来，FragMent中不好获取
    public LinearLayout selectPopViewWindowView;
    public TextView selectPopViewWindowTitleTextView;
    public ScrollView selectPopViewWindowScrollView;
    public LinearLayout selectPopViewWindowContent;
    public Button selectPopViewWindowButton;

    // 用户自定义信息
    public String userTag = "";
    public String groupTag = "";

    public SelectView(Context context) {
        super(context);
        setDefaultVars(context);
    }

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultVars(context);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaultVars(context);
    }

    public SelectViewListener getSelectViewListener() {
        return selectViewListener;
    }

    public void setSelectViewListener(SelectViewListener selectViewListener) {
        if (selectViewListener != null) {
            this.selectViewListener = selectViewListener;
        } else {
            this.selectViewListener = new SelectViewListener() {
                @Override
                public void popViewWillShow() {

                }

                @Override
                public void popViewDidShow() {

                }

                @Override
                public void popViewWillHidden() {

                }

                @Override
                public void popViewDidHidden() {

                }

                @Override
                public void itemSelectedChange() {

                }

                @Override
                public boolean itemAllowClick(MyButton btn) {
                    return false;
                }
            };
        }
    }

    // 初始化
    public void setDefaultVars(Context context) {
        try {
            myContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myFunc = new MyFunction();
            myHandler = new MyHandler();

            // 保存选中的索引
            itemSelectedIndexArr = new ArrayList<>();

            // 初始化
            selectViewWidth = 0;
            selectViewHeight = 50;// 自身按钮高度（与XML中的配置一致）
            itemAllowSelectMinCount = 1;// 最少选择个数
            itemAllowSelectMaxCount = 1;// 最多选择个数
            itemPerRowCount = 1;// 每行控件个数
            itemBtnArr = new ArrayList<>();

            selectLabelMinWidth = 10;// 控件最小宽度
            selectLabelMaxWidth = 9999;// 自身按钮最大宽度
            selectLabelTitle = "请选择";// 弹出框的标题
            selectPopViewTitle = "请选择";// 控件自身的默认值，比如随机、请选择等

            // 弹框事件
            popViewListener = new PopViewListener() {

                @Override
                public void popViewDidShow() {
                    popViewDidShow_This();
                }

                @Override
                public boolean popViewAllowHidden() {
                    // TODO Auto-generated method stub
                    return true;
                }

                @Override
                public void popViewDidHidden() {
                    popViewDidHidden_This();
                }
            };
        } catch (Exception e) {
            System.out.println("SelectView setDefaultVars Exception:" + e);
        }
    }

    // 设置默认样式
    public void setDefaultStyle() {
        // 一般在布局器中直接设置
    }

    // 标题按钮点击
    public void selectBtnClick() {
        if (itemTitleArr.size() == 0) {
            return;
        }

        // 加载弹窗视图
        if (selectPopViewWindowView == null) {
            selectPopViewWindowView = (LinearLayout) mInflater.inflate(R.layout.public_selectview_window, null, false);
            selectPopViewWindowTitleTextView = (TextView) selectPopViewWindowView
                    .findViewById(R.id.SelectViewWindowTitle);
            selectPopViewWindowScrollView = (ScrollView) selectPopViewWindowView
                    .findViewById(R.id.SelectViewWindowScrollView);
            selectPopViewWindowContent = (LinearLayout) selectPopViewWindowView
                    .findViewById(R.id.SelectViewWindowContent);
            selectPopViewWindowButton = (Button) selectPopViewWindowView.findViewById(R.id.SelectViewWindowButton);

            // 绑定数据事件
            selectPopViewWindowTitleTextView.setText(selectPopViewTitle);
            selectPopViewWindowButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    popViewBtnClick();
                }
            });

            // 添加到视图上
            selectPopViewWindowContent.removeAllViews();
            for (int i = 0; i < itemBtnArr.size(); i++) {
                if (itemPerRowCount == 1) {
                    MyButton btn = itemBtnArr.get(i);
                    selectPopViewWindowContent.addView(btn);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                    layoutParams.setMargins(0, myFunc.dip2px(myContext, 2), 0, myFunc.dip2px(myContext, 2));
                    btn.setLayoutParams(layoutParams);
                } else {
                    if (i % itemPerRowCount == 0) {
                        LinearLayout linearLayout = new LinearLayout(myContext);
                        linearLayout.setLayoutParams(
                                new LinearLayout.LayoutParams(-1, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        int maxIndex = Math.min(i + itemPerRowCount, itemBtnArr.size()) - 1;
                        for (int j = i; j <= maxIndex; j++) {

                            MyButton btn = itemBtnArr.get(j);
                            btn.setLayoutParams(
                                    new LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                                            j == maxIndex ? itemPerRowCount - (maxIndex % itemPerRowCount) : 1));
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
                            layoutParams.setMargins(0, myFunc.dip2px(myContext, 2), 0, myFunc.dip2px(myContext, 2));
                            btn.setLayoutParams(layoutParams);
                            linearLayout.addView(btn);
                        }
                        selectPopViewWindowContent.addView(linearLayout);
                    }
                }
            }
        }

        if (selectPopView != null && selectPopViewWindowView != null) {
            selectPopView.setWindowView(selectPopViewWindowView);
            selectPopView.setPopViewListener(this.popViewListener);
            if (this.selectViewListener != null) {
                selectViewListener.popViewWillShow();
            }
            selectPopView.showPopView();
        }
    }

    // 弹出框确定按钮点击
    public void popViewBtnClick() {
        if (this.selectViewListener != null) {
            selectViewListener.popViewWillHidden();
        }
        selectPopViewWindowButton.setEnabled(false);
        selectPopView.hiddenPopView();
    }

    public void popViewDidShow_This() {
        if (this.selectViewListener != null) {
            selectViewListener.popViewDidShow();
        }
        if (selectPopViewWindowButton != null) {
            selectPopViewWindowButton.setEnabled(true);
        }
    }

    public void popViewDidHidden_This() {
        if (this.selectViewListener != null) {
            selectViewListener.popViewDidHidden();
        }
        if (selectPopViewWindowButton != null) {
            selectPopViewWindowButton.setEnabled(true);
        }
    }

    // 绘制
    public void reloadData() {
        try {
            if (this.itemTitleArr == null) {
                return;
            }

            if (this.itemValueArr == null) {
                return;
            }

            if (this.itemTitleArr.size() != this.itemValueArr.size()) {
                return;
            }

            // 选中项
            itemSelectedIndexArr.clear();
            if (itemAllowSelectMinCount > 0 && this.itemSelectedIndexArr.size() == 0 && itemTitleArr.size() > 0) {
                this.itemSelectedIndexArr.add("0");
            }

            // 控件绑定
            // 按钮
            selectLabel = (TextView) this.findViewById(R.id.SelectLabel);

            // 分割线
            selectSplitLine = this.findViewById(R.id.SelectSplitLine);

            // 按钮
            selectBtn = (ImageButton) this.findViewById(R.id.SelectBtn);
            selectBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectBtnClick();
                }
            });

            // 绘制自身按钮
            setDefaultStyle();
            drawSelectView();

            // 绘制弹出窗口按钮
            itemBtnArr.clear();
            for (int i = 0; i < this.itemTitleArr.size(); i++) {
                MyButton btn = (MyButton) mInflater.inflate(R.layout.public_selectview_checkbox, null, false);
                btn.setText(this.itemTitleArr.get(i));
                btn.groupTag = "CheckBox";
                btn.setTag(i);
                btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        checkBoxClick(v);
                    }
                });
                if (this.itemSelectedIndexArr.contains(String.valueOf(i))) {
                    btn.setSelected(true);
                }

                itemBtnArr.add(btn);
            }

            selectPopViewWindowView = null;
        } catch (Exception e) {
            System.out.println("SelectView reloadData Exception:" + e);
        }
    }

    // 绘制自身按钮
    public void drawSelectView() {
        try {
            String titleText = myFunc.joinArr(itemSelectedTitleArr(), "、");
            if (titleText.length() == 0) {
                titleText = this.selectLabelTitle;
            }
            selectLabel.setText(titleText);

            // 计算Lable宽度
            LayoutParams selectLabelLayoutParams = (LayoutParams) selectLabel.getLayoutParams();
            if (selectViewWidth == 1) {//填满
                selectLabelLayoutParams.width = LayoutParams.MATCH_PARENT;
            } else if (selectViewWidth == 0) {//自适应
                int itemTitleLabelWidth = myFunc.px2dip(myContext, myFunc.getTextViewLength(selectLabel, titleText)
                        + selectLabel.getPaddingLeft() + selectLabel.getPaddingRight() + 1);
                selectLabelLayoutParams.width = myFunc.dip2px(myContext,
                        Math.max(selectLabelMinWidth, Math.min(itemTitleLabelWidth, selectLabelMaxWidth)));
            } else {//定长
                selectLabelLayoutParams.width = myFunc.dip2px(myContext, selectViewWidth - 12);
            }
            selectLabelLayoutParams.height = myFunc.dip2px(myContext, selectViewHeight - 1);
            selectLabel.setLayoutParams(selectLabelLayoutParams);

            // 自身宽高度
            ViewGroup.LayoutParams thisLayoutParams = this.getLayoutParams();
            if (selectViewWidth == 1) {
                thisLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                thisLayoutParams.width = selectLabelLayoutParams.width + myFunc.dip2px(myContext, 12);
            }
            thisLayoutParams.height = myFunc.dip2px(myContext, selectViewHeight);
            this.setLayoutParams(thisLayoutParams);
        } catch (Exception e) {
            System.out.println("SelectView drawSelectView Exception:" + e);
        }
    }

    // 获取选中的索引值
    public List<String> itemSelectedIndexArr() {
        return new ArrayList<>(this.itemSelectedIndexArr);
    }

    // 获取选中的标题
    public List<String> itemSelectedTitleArr() {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < this.itemSelectedIndexArr.size(); i++) {
            arr.add(this.itemTitleArr.get(Integer.parseInt(this.itemSelectedIndexArr.get(i))));
        }

        return arr;
    }

    // 获取选中的值
    public List<String> itemSelectedValueArr() {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < this.itemSelectedIndexArr.size(); i++) {
            arr.add(this.itemValueArr.get(Integer.parseInt(this.itemSelectedIndexArr.get(i))));
        }

        return arr;
    }

    // CheckBox选择
    public void checkBoxClick(View view) {
        try {
            MyButton btn = (MyButton) view;

            // 是否允许点击
            if (this.selectViewListener != null) {
                if (!this.selectViewListener.itemAllowClick(btn)) {
                    return;
                }
            }

            // 能否取消
            if (btn.isSelected()) {
                if (this.itemAllowSelectMinCount == 0) {
                    btn.setSelected(false);
                } else {
                    if (this.itemSelectedIndexArr.size() > this.itemAllowSelectMinCount) {
                        btn.setSelected(false);
                    }
                }
            } else {
                // 能否选中
                if (this.itemAllowSelectMaxCount == 1) {// 单选情况
                    for (int i = 0; i < itemBtnArr.size(); i++) {
                        MyButton tempBtn = itemBtnArr.get(i);
                        if (tempBtn.equals(btn)) {
                            tempBtn.setSelected(true);
                        } else {
                            tempBtn.setSelected(false);
                        }
                    }
                } else {
                    if (this.itemSelectedIndexArr.size() < this.itemAllowSelectMaxCount) {
                        btn.setSelected(true);
                    }
                }
            }

            updateItemSelectedIndexArr();
        } catch (Exception e) {
            System.out.println("SelectView checkBoxClick Exception:" + e);
        }
    }

    // 更新并通知选中情况更新
    public void updateItemSelectedIndexArr() {
        try {
            // 更新选中数组
            String oldSelectTitle = myFunc.joinArr(itemSelectedTitleArr());
            String oldSelectValue = myFunc.joinArr(itemSelectedValueArr());
            this.itemSelectedIndexArr.clear();
            for (int i = 0; i < itemBtnArr.size(); i++) {
                MyButton tempBtn = itemBtnArr.get(i);
                if (tempBtn.isSelected()) {
                    this.itemSelectedIndexArr.add(String.valueOf(i));
                }
            }
            String newSelectTitle = myFunc.joinArr(itemSelectedTitleArr());
            String newSelectValue = myFunc.joinArr(itemSelectedValueArr());

            // 绘制自身按钮 发出通知
            if (!oldSelectTitle.equals(newSelectTitle) || !oldSelectValue.equals(newSelectValue)) {
                drawSelectView();
                if (this.selectViewListener != null) {
                    this.selectViewListener.itemSelectedChange();
                }
            }
        } catch (Exception e) {
            System.out.println("SelectView updateItemSelectedIndexArr Exception:" + e);
        }
    }

    // 选中指定的控件
    public void selectItemByIndex(int index) {
        List<String> arr = new ArrayList<>();
        arr.add(String.valueOf(index));
        selectItemByIndexArr(arr);
    }

    public void selectItemByTitle(String title) {
        int selectedCount = 0;
        for (int i = 0; i < itemBtnArr.size(); i++) {
            MyButton tempBtn = itemBtnArr.get(i);
            if (tempBtn.getText().toString().equals(title) && selectedCount < this.itemAllowSelectMaxCount) {
                selectedCount++;
                tempBtn.setSelected(true);
            } else {
                tempBtn.setSelected(false);
            }
        }
        updateItemSelectedIndexArr();
    }

    // 根据索引数组选中多个控件 单个索引必须是NSString
    public void selectItemByIndexArr(List<String> indexArr) {
        if (indexArr == null || indexArr.size() == 0) {
            return;
        }

        if (itemBtnArr.size() == 0) {
            return;
        }

        int selectedCount = 0;
        for (int i = 0; i < itemBtnArr.size(); i++) {
            MyButton tempBtn = itemBtnArr.get(i);
            if (indexArr.contains(String.valueOf(i)) && selectedCount < this.itemAllowSelectMaxCount) {
                selectedCount++;
                tempBtn.setSelected(true);
            } else {
                tempBtn.setSelected(false);
            }
        }
        updateItemSelectedIndexArr();
    }

    // 根据标题数组选中多个控件 单个标题必须是NSString
    public void selectItemByTitleArr(List<String> titleArr) {
        if (titleArr == null || titleArr.size() == 0) {
            return;
        }

        if (itemTitleArr == null || itemTitleArr.size() == 0) {
            return;
        }

        List<String> indexArr = new ArrayList<>();
        for (int i = 0; i < itemTitleArr.size(); i++) {
            if (titleArr.contains(itemTitleArr.get(i))) {
                indexArr.add(String.valueOf(i));
            }
        }

        selectItemByIndexArr(indexArr);
    }

    // 根据值数组选中多个控件 单个值必须是NSString
    public void selectItemByValueArr(List<String> valueArr) {
        if (valueArr == null || valueArr.size() == 0) {
            return;
        }

        if (itemValueArr == null || itemValueArr.size() == 0) {
            return;
        }

        List<String> indexArr = new ArrayList<>();
        for (int i = 0; i < itemValueArr.size(); i++) {
            if (valueArr.contains(itemValueArr.get(i))) {
                indexArr.add(String.valueOf(i));
            }
        }

        selectItemByIndexArr(indexArr);
    }

    // 清空选项
    public void clearItem() {
        itemTitleArr = new ArrayList<>();
        itemValueArr = new ArrayList<>();
        reloadData();
    }

    // 设置单选
    public void setSingleSelect() {
        this.itemAllowSelectMinCount = 1;
        this.itemAllowSelectMaxCount = 1;
    }

    // 设置多选
    public void setMultSelect() {
        this.itemAllowSelectMinCount = 0;
        this.itemAllowSelectMaxCount = 100000;
    }

    // 设置最多可选择一个
    public void setMaxOneSelect() {
        this.itemAllowSelectMinCount = 0;
        this.itemAllowSelectMaxCount = 1;
    }
}
