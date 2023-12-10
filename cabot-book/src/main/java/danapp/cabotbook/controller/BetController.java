package danapp.cabotbook.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import danapp.cabotbook.globals.GlobalUserList;
import danapp.cabotbook.model.User;
import danapp.cabotbook.people.UserApp;
import danapp.cabotbook.repository.UserRepository;
import danapp.cabotbook.resource.UserRequestFromNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@RestController
public class BetController {
    private final UserRepository userRepository;

    public BetController(UserRepository userRepository) {this.userRepository = userRepository;};

    @PostMapping("/getbets")
    public ResponseEntity<UserApp> getBets(@RequestBody String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);
            UserApp userToGetBets = loadUserApp(userRequestFromNode);

            userToGetBets.addBetToPending("Ryan", 1.4, 100, "Ryan vs Mau");
            userToGetBets.addBetToPending("Jim", 2.4, 100, "Jim vs Rum");
            userToGetBets.approvePendingBet("Ryan vs Mau");

            return ResponseEntity.status(200).body(userToGetBets);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/addbet")
    public ResponseEntity<UserApp> addBet(@RequestBody String jsonData) {
        //
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);
            UserApp userToGetBets = loadUserApp(userRequestFromNode);

            userToGetBets.addBetToPending("Ryan", 1.4, 100, "Ryan vs Mau");
            userToGetBets.addBetToPending("Jim", 2.4, 100, "Jim vs Rum");
            userToGetBets.approvePendingBet("Ryan vs Mau");

            return ResponseEntity.status(200).body(userToGetBets);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    public UserApp loadUserApp(UserRequestFromNode userRequestFromNode) {
        ArrayList<UserApp> globalUserLists = GlobalUserList.getInstance().getUsersOnApp();
        if(UserApp.doesUserExist(globalUserLists, userRequestFromNode.getId())) {
            for (UserApp globalUserList : globalUserLists) {
                if (Objects.equals(globalUserList.getKindeId(), userRequestFromNode.getId())) {
                    return globalUserList;
                }
            }
        }
        Optional<User> userMightExist = userRepository.findByKindeId(userRequestFromNode.getId());
        if(userMightExist.isPresent()) {
            User existingUser = userMightExist.get();
            return UserApp.fromDatabaseModel(existingUser);
        } else {
            User newUser = new User();
            newUser.setName(userRequestFromNode.getGiven_name());
            newUser.setBalance(0);
            newUser.setKindeId(userRequestFromNode.getId());
            ArrayList<String> transactionArrayList = new ArrayList<>();
            newUser.setTransactionHistory(transactionArrayList);

            try {
                User savedUser = userRepository.save(newUser);
                return UserApp.fromDatabaseModel(savedUser);
            } catch (Exception e) {
                e.printStackTrace();
                return new UserApp();
            }



        }
    }
}
