package ptit.nttrung.chatusefirebase.model;

import java.util.ArrayList;

/**
 * Created by TrungNguyen on 9/19/2017.
 */

public class ListFriend {
    private ArrayList<Friend> listFriend;

    public ArrayList<Friend> getListFriend() {
        return listFriend;
    }

    public ListFriend() {
        listFriend = new ArrayList<>();
    }

    public String getAvataById(String id) {
        for (Friend friend : listFriend) {
            if (id.equals(friend.getId())) {
                return friend.getAvata();
            }
        }
        return "";
    }

    public void setListFriend(ArrayList<Friend> listFriend) {
        this.listFriend = listFriend;
    }
}
