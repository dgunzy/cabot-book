package danapp.cabotbook.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import danapp.cabotbook.bets.Bet;
import danapp.cabotbook.model.User;

import danapp.cabotbook.people.UserApp;
import danapp.cabotbook.repository.UserRepository;
import danapp.cabotbook.resource.UserRequestFromNode;
import danapp.cabotbook.globals.GlobalUserList;
import org.springframework.beans.BeanUtils;
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
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userRepository.findAll());
    }

    @PostMapping("/adduser")
    public ResponseEntity<User> createUser(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);

            User user = new User();
            System.out.println(userRequestFromNode.getGiven_name());
            user.setName(userRequestFromNode.getGiven_name());
            user.setBalance(0);
            user.setKindeId(userRequestFromNode.getId());

            return ResponseEntity.status(201).body(this.userRepository.save(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }



    @PostMapping("/updatebalance/{balance}")
    public ResponseEntity<String> updateUserBalance(@RequestBody String jsonData, @PathVariable int balance) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);
        UserApp userToUpdate = loadUserApp(userRequestFromNode);
        userToUpdate.setBalance(userToUpdate.getBalance() + balance );
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
    public ResponseEntity getUserById(@PathVariable String id) {

        Optional<User> user = this.userRepository.findById(id);

        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else  {
            return ResponseEntity.ok("The user with the id: " + id + " was not found.");
        }
    }
    @DeleteMapping("/userdelete/{id}")
    public ResponseEntity deleteUserById(@PathVariable String id) {

        Optional<User> user = this.userRepository.findById(id);

        if(user.isPresent()) {
            this.userRepository.deleteById(id);
            return ResponseEntity.ok("Success deleting user");
        } else  {
            return ResponseEntity.ok("The user with the id: " + id + " was not found.");
        }
    }


    @PostMapping("/checkuser")
    public ResponseEntity<User> checkUser(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);

            Optional<User> userMightExist = userRepository.findByKindeId(userRequestFromNode.getId());

            if (userMightExist.isPresent()) {
                User existingUser = userMightExist.get();
                System.out.println("User found!");
                return ResponseEntity.ok(existingUser);

            }

            User newUser = new User();
            newUser.setName(userRequestFromNode.getGiven_name());
            newUser.setBalance(0);
            newUser.setKindeId(userRequestFromNode.getId());
            ArrayList<String> transactionArrayList = new ArrayList<>();
            newUser.setTransactionHistory(transactionArrayList);

            User savedUser = userRepository.save(newUser);

            return ResponseEntity.status(201).body(savedUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/testbetlogic")
    public void testBetLogic(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequestFromNode userRequestFromNode = objectMapper.readValue(jsonData, UserRequestFromNode.class);


            UserApp dan = loadUserApp(userRequestFromNode);


                Bet firstMatch  = new Bet("Ryan Mau");

                firstMatch.addBetOptionAmerican("Mau", -150.0);

                firstMatch.addBetOptionAmerican("Ryan", 125.0);


                dan.addBetToPending("Mau", firstMatch.getBetOddsDecimal("Mau"), 50, "Mau vs Ryan");

                System.out.println(dan.getBalance());
                dan.viewPendingBets();

                dan.approvePendingBet("Mau");

                dan.viewPendingBets();
                System.out.println("Approved Bets below");

                dan.viewApprovedBets();

                dan.winApprovedBet("Mau");

                Thread.sleep(10000);



                System.out.println("Graded bets below");



                System.out.println("Transaction History string:" + dan.getTransactionHistory());


                System.out.println(dan.getBalance());



                updateUser(dan.databaseUserFromUserApp());





        } catch (JsonProcessingException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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


    public User updateUser(User userWithUpdate) {
        User userToUpdate = userRepository.findByKindeId(userWithUpdate.getKindeId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(userWithUpdate, userToUpdate);

        return userRepository.save(userToUpdate);

    }







}
