package net.warvale.bungee.users;

import net.warvale.bungee.utils.misc.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDataUpdate {

    private UserData userData;

    private List<Pair<String, Object>> data = new ArrayList<>();

    @SafeVarargs
    public UserDataUpdate(UserData userData, Pair<String, Object>... data) {
        this.userData = userData;

        Collections.addAll(this.data, data);
    }

    public void queue() {
        UserManager.addPendingUpdate(this);
    }

    public UserData getUserData() {
        return userData;
    }

    public List<Pair<String, Object>> getData() {
        return data;
    }

}
