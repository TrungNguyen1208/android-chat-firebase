package ptit.nttrung.chatusefirebase.base;

/**
 * Created by TrungNguyen on 8/29/2017.
 */

public interface BaseView<T> {
    void setPresenter(T presenter);

    void showProgressDialog(String message);

    void hideProgressDialog();

    void makeToast(String message);
}
