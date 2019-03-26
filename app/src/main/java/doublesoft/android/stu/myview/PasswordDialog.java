package doublesoft.android.stu.myview;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class PasswordDialog extends Dialog {
    private MyNumKeyboard myNumKeyboard = null;
    private PasswordInputView mInputView = null;
    private Fragment fromFragment = null;

    private OnInputComplete onInputComplete;

    public PasswordDialog(Fragment fragment, OnInputComplete onInputComplete) {
        super(fragment.getContext());
        fromFragment = fragment;
        this.onInputComplete = onInputComplete;
        init();
    }

    private void init() {
        setContentView(doublesoft.android.stu.R.layout.dialog_password);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mInputView = (PasswordInputView) findViewById(doublesoft.android.stu.R.id.PayPasswordEditText);

        findViewById(doublesoft.android.stu.R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if (findViewById(doublesoft.android.stu.R.id.mynumkeyboard) != null) {
            myNumKeyboard = new MyNumKeyboard(fromFragment.getActivity(), getWindow().getDecorView());
        }
        mInputView.myNumKeyboard = myNumKeyboard;
        mInputView.myNumKeyboardFHType = 2;
        mInputView.setInputLimit(6);

        mInputView.init();


        mInputView.setMyNumEditTextListener(new MyNumEditTextListener() {

            @Override
            public void myNumEditTextOKBtnOnClick() {
                submit();
            }

            @Override
            public void myNumEditTextDidBeginEditing() {
            }

            @Override
            public void myNumEditTextDidEndEditing() {
            }

            @Override
            public void myNumEditTextDidChange() {
            }
        });

        Button payPasswordButton = (Button) findViewById(doublesoft.android.stu.R.id.PayPasswordButton);
        payPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myNumKeyboard.showKeyboard();
        mInputView.requestFocus();
    }

    private void submit() {
        String password = mInputView.getText().toString();
        if (password.length() != 6) {
            Toast.makeText(getContext(), "请输入6位数支付密码", Toast.LENGTH_LONG).show();
        } else {
            onInputComplete.onComplete(password);
            dismiss();
        }
    }

    public interface OnInputComplete {
        void onComplete(String str);
    }
}
