package com.danertu.tools;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.TextView;

public class GoodsEditTWatcher implements TextWatcher {

    private boolean resetText;
    private String tmp = "";
    private TextView sign_text;

    public GoodsEditTWatcher(TextView sign_text) {
        this.sign_text = sign_text;
    }

    @Override
    public void afterTextChanged(Editable arg0) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        tmp = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!resetText) {
            if (s.toString().contains("\n")) {
                resetText = true;
                sign_text.setText(tmp);
                sign_text.invalidate();
            }
            if (count == 2 && !EmojiFilter.containsEmoji(s.toString().substring(start, start + 2))) {
                resetText = true;
                sign_text.setText(tmp);
                sign_text.invalidate();
                if (sign_text.getText().length() > 1)
                    Selection.setSelection((Spannable) sign_text.getText(), sign_text.getText().length());
            }
        } else {
            resetText = false;
        }
    }

}
