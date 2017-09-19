package ptit.nttrung.chatusefirebase.ulti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import ptit.nttrung.chatusefirebase.R;


/**
 * Created by TrungNguyen on 7/25/2017.
 */

public class ActivityUtils {

    public static Dialog commontDialog;

    public static Dialog getCommontDialog() {
        return commontDialog;
    }

    public static void setCommontDialog(Dialog commontDialog) {
        ActivityUtils.commontDialog = commontDialog;
    }

    public static Dialog alert(Context context, String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);

        if (!title.isEmpty()) {
//            adb.setIcon(R.drawable.alert_dialog_icon);
            adb.setTitle(title);
        }
        adb.setMessage(message);
        adb.setPositiveButton(context.getString(R.string.ok), null);
        return adb.show();
    }
//
//    public static void showOkConfirmAlerDialog(Context context,
//                                               String title,
//                                               String content,
//                                               String btnOkText,
//                                               View.OnClickListener okClickListener) {
//        String messageTitle;
//        String mesageContent;
//        String txtOkContent;
//        if (title.isEmpty()) {
//            messageTitle = "";
//        } else {
//            messageTitle = title;
//        }
//
//        if (content.isEmpty()) {
//            mesageContent = "";
//        } else {
//            mesageContent = content;
//        }
//
//        if (btnOkText.isEmpty()) {
//            txtOkContent = context.getString(R.string.ok);
//        } else {
//            txtOkContent = btnOkText;
//        }
//    }

    public static void showOkCancelConfirmAlertDialog(Context context,
                                                      boolean cancelable,
                                                      String title,
                                                      String content,
                                                      String btnOkText,
                                                      String btnCancelText,
                                                      DialogInterface.OnClickListener okClickListene,
                                                      DialogInterface.OnClickListener cancelClickListene) {
        String msgTitle;
        String msgContent;
        String txtOkContent;
        String txtCancelContent;

        if (title.isEmpty()) {
            msgTitle = "";
        } else {
            msgTitle = title;
        }

        if (content.isEmpty()) {
            msgContent = "";
        } else {
            msgContent = content;
        }

        if (btnOkText.isEmpty()) {
            txtOkContent = "";
        } else {
            txtOkContent = btnOkText;
        }

        if (btnCancelText.isEmpty()) {
            txtCancelContent = "";
        } else {
            txtCancelContent = btnCancelText;
        }
        //Title
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (null != msgTitle && !msgTitle.isEmpty()) {
            builder.setTitle(msgTitle);
        }
        // Content
        if (null != msgContent && !msgContent.isEmpty()) {
            builder.setMessage(msgContent);
        }

        builder.setCancelable(cancelable);

        builder.setNegativeButton(btnOkText, okClickListene);
        builder.setPositiveButton(btnCancelText, cancelClickListene);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void alert(Context context, boolean hasTitle, String title,
                             String message,
                             DialogInterface.OnClickListener yes_onclicklistener,
                             DialogInterface.OnClickListener no_onclicklistener) {

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        if (hasTitle) {
            title = "";
            if (!title.isEmpty()) {
                adb.setTitle(title);
            }
        }
        adb.setMessage(message);
        adb.setPositiveButton(context.getString(R.string.yes), yes_onclicklistener);
        adb.setNegativeButton(context.getString(R.string.dismiss), no_onclicklistener);
        // adb.create();
        adb.show();
    }

    public static void showAlertCofirm(Context context, boolean hasTitle,
                                       String title,
                                       String message,
                                       DialogInterface.OnClickListener yes_onclicklistener) {

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        if (hasTitle) {
            title = "";
            if (!title.isEmpty()) {
                adb.setTitle(title);
            }
        }
        adb.setMessage(message);
        adb.setPositiveButton(context.getString(R.string.yes), yes_onclicklistener);
        // adb.create();
        adb.show();
    }
}
