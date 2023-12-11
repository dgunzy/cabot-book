package danapp.cabotbook.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import danapp.cabotbook.Auth.AppConfig;
import danapp.cabotbook.bets.Bet;
import danapp.cabotbook.globals.GlobalBetsList;
import danapp.cabotbook.model.User;

import danapp.cabotbook.people.UserApp;
import danapp.cabotbook.repository.UserRepository;
import danapp.cabotbook.resource.UserRequestFromNode;
import danapp.cabotbook.globals.GlobalUserList;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




    @GetMapping("/users")
    public ResponseEntity<String> getAllUsers( @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) throws JsonProcessingException {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        ArrayList<UserApp> globalUserLists = GlobalUserList.getInstance().getUsersOnApp();

        System.out.println(globalUserLists.size());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonUsers = objectMapper.writeValueAsString(globalUserLists);
            System.out.println(jsonUsers);

            return ResponseEntity.status(200).body(jsonUsers);
        } catch (Exception e) {
            return  ResponseEntity.status(500).body("Error getting users");
        }
    }




    @PostMapping("/updatebalance/{balance}/{kindeId}")
    public ResponseEntity<String> updateUserBalance(@RequestBody String jsonData, @PathVariable int balance,@PathVariable String kindeId,  @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) throws JsonProcessingException {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);
            String adminName = userRequestFromNode.getGiven_name();
            UserApp userToUpdate = loadUserAppWithId(kindeId);
            userToUpdate.setBalance(userToUpdate.getBalance() + balance );
            userToUpdate.getTransactionHistory().add("Your account was manually ajusted " + balance + " by " + adminName);
            GlobalUserList.getInstance().addUserToGlobalList(userToUpdate);
            updateUser(userToUpdate.databaseUserFromUserApp());
            String responseBody =  "Success updating user balance! Balance is " + userToUpdate.getBalance();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            e.getLocalizedMessage();
            String responseBody =  "Failed to updated balance!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseBody);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUserById(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }

        Optional<User> user = this.userRepository.findById(id);

        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else  {
            return ResponseEntity.ok("The user with the id: " + id + " was not found.");
        }
    }
    @DeleteMapping("/userdelete/{id}")
    public ResponseEntity deleteUserById(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }

        Optional<User> user = this.userRepository.findById(id);

        if(user.isPresent()) {
            this.userRepository.deleteById(id);
            return ResponseEntity.ok("Success deleting user");
        } else  {
            return ResponseEntity.ok("The user with the id: " + id + " was not found.");
        }
    }


    @PostMapping("/checkuser")
    public ResponseEntity<UserApp> checkUser(@RequestBody String jsonData, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);

            UserApp user = loadUserApp(userRequestFromNode);
            return ResponseEntity.status(201).body(user);
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

            UserApp userToLoad = UserApp.fromDatabaseModel(existingUser);

            GlobalUserList.getInstance().addUserToGlobalList(userToLoad);
            return userToLoad;
        } else {
            User newUser = new User();
            newUser.setName(userRequestFromNode.getGiven_name());
            newUser.setBalance(0);
            newUser.setKindeId(userRequestFromNode.getId());
            ArrayList<String> transactionArrayList = new ArrayList<>();
            newUser.setTransactionHistory(transactionArrayList);

            try {
                User savedUser = userRepository.save(newUser);
                UserApp newUserInMemory =  UserApp.fromDatabaseModel(savedUser);
                GlobalUserList.getInstance().addUserToGlobalList(newUserInMemory);
                return newUserInMemory;
            } catch (Exception e) {
                e.printStackTrace();
                return new UserApp();
            }
        }
    }
    public UserApp loadUserAppWithId(String kindeId) {
        ArrayList<UserApp> globalUserList = GlobalUserList.getInstance().getUsersOnApp();

        if (UserApp.doesUserExist(globalUserList, kindeId)) {
            for (UserApp globalUser : globalUserList) {
                if (Objects.equals(globalUser.getKindeId(), kindeId)) {
                    return globalUser;
                }
            }
        }
        Optional<User> userMightExist = userRepository.findByKindeId(kindeId);
        if (userMightExist.isPresent()) {
            User existingUser = userMightExist.get();

            UserApp userToLoad = UserApp.fromDatabaseModel(existingUser);

            GlobalUserList.getInstance().addUserToGlobalList(userToLoad);
            return userToLoad;
        } else {
            return null;
        }
    }


    public void updateUser(User userWithUpdate) {
        User userToUpdate = userRepository.findByKindeId(userWithUpdate.getKindeId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(userWithUpdate, userToUpdate);

        userRepository.save(userToUpdate);

    }







}
