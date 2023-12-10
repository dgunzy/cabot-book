package danapp.cabotbook.globals;

import danapp.cabotbook.people.UserApp;

import java.util.ArrayList;

public class GlobalUserList {
    private static GlobalUserList instance;

    private ArrayList<UserApp> usersOnApp;

    private GlobalUserList() {
        usersOnApp = new ArrayList<>();
    }

    public static GlobalUserList getInstance() {
        if (instance == null) {
            instance = new GlobalUserList();
        }
        return instance;
    }

    public ArrayList<UserApp> getUsersOnApp() {
        return usersOnApp;
    }

    public void addUserToGlobalList(UserApp userApp) {
        usersOnApp.add(userApp);
    }
}
