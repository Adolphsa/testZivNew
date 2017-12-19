package com.yeejay.yplay.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeejay.yplay.R;

/**
 * Created by xjg on 2017/12/19.
 */

public class CustomGenderDialog extends Dialog {

    public CustomGenderDialog(Context context) {
        super(context);
    }

    public CustomGenderDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            contentView = inflater.inflate(R.layout.dialog_content_gender_layout, null);
        }

        public View getContentView() {
            return contentView;
        }

        public CustomGenderDialog create() {

            // instantiate the dialog with the custom Theme
            final CustomGenderDialog dialog = new CustomGenderDialog(context,R.style.Dialog);
            dialog.addContentView(contentView, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            dialog.setContentView(contentView);
            return dialog;
        }
    }
}
