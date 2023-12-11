package danapp.cabotbook.globals;

import danapp.cabotbook.people.UserApp;

import java.util.ArrayList;
import java.util.Optional;

public class GlobalUserList {

    //Global singleton list of users in memory
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
        if (usersOnApp == null) {
            usersOnApp = new ArrayList<>();
        }

        Optional<UserApp> existingUser = usersOnApp.stream()
                .filter(u -> u.getKindeId().equals(userApp.getKindeId()))
                .findFirst();

        if (existingUser.isPresent()) {
            UserApp userToUpdate = existingUser.get();
            userToUpdate.updateFrom(userApp);
        } else {
            usersOnApp.add(userApp);
        }
    }
}
