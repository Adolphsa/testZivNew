package com.yeejay.yplay.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 对话框展示工具
 * Created by Administrator on 2017/11/20.
 */

public class DialogUtils {

    //邀请提示对话框
    public static Dialog showInviteDialogInfo(Context context, String msg){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

        return dialog;

    }


}
