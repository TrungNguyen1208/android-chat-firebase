package ptit.nttrung.chatusefirebase.model;

import java.util.ArrayList;

/**
 * Created by TrungNguyen on 9/19/2017.
 */

public class Consersation {
    private ArrayList<Message> listMessageData;

    public Consersation() {
        listMessageData = new ArrayList<>();
    }

    public ArrayList<Message> getListMessageData() {
        return listMessageData;
    }
}
