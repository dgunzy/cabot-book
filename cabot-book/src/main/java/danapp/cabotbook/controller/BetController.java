package danapp.cabotbook.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import danapp.cabotbook.Auth.AppConfig;
import danapp.cabotbook.bets.Bet;
import danapp.cabotbook.bets.BetOdds;
import danapp.cabotbook.bets.OddsConverter;
import danapp.cabotbook.bets.PlacedBet;
import danapp.cabotbook.globals.GlobalBetsList;
import danapp.cabotbook.globals.GlobalUserList;
import danapp.cabotbook.model.User;
import danapp.cabotbook.people.UserApp;
import danapp.cabotbook.repository.UserRepository;
import danapp.cabotbook.resource.BetCreateRequestFromNode;
import danapp.cabotbook.resource.UserRequestFromNode;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

@RestController
public class BetController {
    private final UserRepository userRepository;

    public BetController(UserRepository userRepository) {this.userRepository = userRepository;};

    @PostMapping("/getbets")
    public ResponseEntity<String> getBets(@RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        try {

            ArrayList<Bet> currentBets = GlobalBetsList.getInstance().getCurrentBets();

            ArrayList<Bet> betsToShow = new ArrayList<>();

            for(Bet currentBet : currentBets) {
                betsToShow.add(currentBet.clone());
            }

            for (int i = 0; i < betsToShow.size(); i++) {
                Bet clonedBet = betsToShow.get(i).clone();
                betsToShow.set(i, clonedBet);

                for (int j = 0; j < clonedBet.getBetOdds().size(); j++) {
                    double originalOdds = clonedBet.getBetOdds().get(j).getOdds();
                    double americanOdds = OddsConverter.decimalToAmerican(originalOdds);
                    clonedBet.getBetOdds().get(j).setOdds(americanOdds);
                }
            }


            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBets = objectMapper.writeValueAsString(betsToShow);
            System.out.println(jsonBets);
            return ResponseEntity.status(200).body(jsonBets);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Error getting bets");
        }
    }

    @PostMapping("/changebet/{betDescription}/{betSubject}/{odds}")
    public ResponseEntity<String> changeBet(@PathVariable String betSubject, @PathVariable String betDescription,  @PathVariable double odds, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if (!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }

        try {
            ArrayList<Bet> currentBets =  GlobalBetsList.getInstance().getCurrentBets();
            System.out.println(betSubject);
            Iterator<Bet> iterator = currentBets.iterator();
            while (iterator.hasNext()) {
                Bet currentBet = iterator.next();
                if(Objects.equals(currentBet.getName(), betDescription)) {
                    for (int i = 0; i < currentBet.getBetOdds().size(); i++) {
                        if(Objects.equals(currentBet.getBetOdds().get(i).getHorse(), betSubject)) {
                            currentBet.getBetOdds().get(i).setOdds(OddsConverter.americanToDecimal(odds));
                            return ResponseEntity.status(200).body("Odds updated successfully");
                        }
                    }
                    return ResponseEntity.status(404).body("Bet odds not found with that horse");
                }
            }
            return ResponseEntity.status(404).body("Bet not found with that description");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/deletebet/{betDescription}")
    public ResponseEntity<String> deleteBet(@PathVariable String betDescription, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if (!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }

        try {
            GlobalBetsList.getInstance().removeBetFromGlobalList(betDescription);
            return ResponseEntity.status(200).body("Successfully deleted bet");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/addbet")
    public ResponseEntity<String> addBet(@RequestBody BetCreateRequestFromNode betRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if (!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Bad request");
        }
        try {

            ArrayList<BetOdds> oddAndHorses = new ArrayList<BetOdds>();
            for(int i = 0; i < betRequest.getHorse().size(); i++ ) {
                BetOdds betOddsToAdd = new BetOdds(betRequest.getHorse().get(i), OddsConverter.americanToDecimal(betRequest.getOdds().get(i)) );
                oddAndHorses.add(betOddsToAdd);
            }
            Bet newBet = new Bet(betRequest.getName(), oddAndHorses);

            if(checkIfBetNameExists(newBet)) {
                return ResponseEntity.status(500).body("Bet of that name already exists");
            }
            GlobalBetsList.getInstance().addBetToGlobalList(newBet);
            return ResponseEntity.ok().body("Bet added!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding bet");
        }
    }

    @PostMapping("/addbettouserpending/{betSubject}/{betDescription}/{wager}/{odds}")
    public ResponseEntity<String> addBetToUserPending(@PathVariable String betSubject, @PathVariable String betDescription, @PathVariable int wager, @PathVariable double odds, @RequestBody String jsonData, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
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
            user.addBetToPending(betSubject.toLowerCase(), odds, wager, betDescription.toLowerCase());

            GlobalUserList.getInstance().addUserToGlobalList(user);


            return ResponseEntity.status(200).body("Bet added to user");

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Error getting bets");
        }
    }

    @PostMapping("/addbettouserapproved/{betDescription}/{betHorse}/{kindeId}")
    public ResponseEntity<String> addBetToUserApproved( @PathVariable String betDescription,@PathVariable String betHorse, @PathVariable String kindeId, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        try {
            UserApp user = loadUserAppWithId(kindeId);

            user.approvePendingBet(betDescription, betHorse);

            GlobalUserList.getInstance().addUserToGlobalList(user);


            return ResponseEntity.status(200).body("Bet approved to user");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error Loading user");
        }
    }


    @PostMapping("/denypendingbet/{betDescription}/{betHorse}/{kindeId}")
    public ResponseEntity<String> denyPendingBet( @PathVariable String betDescription,@PathVariable String betHorse, @PathVariable String kindeId, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        try {

            UserApp user = loadUserAppWithId(kindeId);

            user.denyPendingBet(betDescription, betHorse);

            GlobalUserList.getInstance().addUserToGlobalList(user);

            return ResponseEntity.status(200).body("Bet removed from user pending bets");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error Loading user");
        }
    }
    @PostMapping("/winapprovedbet/{betDescription}/{betHorse}/{kindeId}")
    public ResponseEntity<String> winApprovedBet( @PathVariable String betDescription,@PathVariable String betHorse, @PathVariable String kindeId, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        try {
            UserApp user = loadUserAppWithId(kindeId);

            user.winApprovedBet(betDescription, betHorse);

            GlobalUserList.getInstance().addUserToGlobalList(user);

            User userToSave = user.databaseUserFromUserApp();

            updateUser(userToSave);

            return ResponseEntity.status(200).body("Bet added to graded bets");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error Loading user");
        }
    }

    @PostMapping("/loseapprovedbet/{betDescription}/{betHorse}/{kindeId}")
    public ResponseEntity<String> loseApprovedBet( @PathVariable String betDescription, @PathVariable String betHorse,@PathVariable String kindeId, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if(!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
        try {
            UserApp user = loadUserAppWithId(kindeId);

            user.loseApprovedBet(betDescription, betHorse);

            GlobalUserList.getInstance().addUserToGlobalList(user);

            User userToSave = user.databaseUserFromUserApp();

            updateUser(userToSave);

            return ResponseEntity.status(200).body("Bet added to graded bets");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error Loading user");
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

    public boolean checkIfBetNameExists(Bet bet) {

        ArrayList<Bet> currentBets =  GlobalBetsList.getInstance().getCurrentBets();

        Iterator<Bet> iterator = currentBets.iterator();
        while (iterator.hasNext()) {
            Bet currentBet = iterator.next();
            if(Objects.equals(currentBet.getName(), bet.getName())) {
               return true;
            }
        }
        return false;
    }

    @PostMapping("/addbettobets/{betName}/{betWager}")
    public ResponseEntity<String> addBetToBets(@RequestBody Bet bet, @RequestHeader(HttpHeaders.AUTHORIZATION) String apiKey) {
        try {
            if (!Objects.equals(apiKey, AppConfig.getApiKey())) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Bad request");
        }
        try {
            if(checkIfBetNameExists(bet)) {
                return ResponseEntity.status(500).body("Bet of that name already exists");
            }
            GlobalBetsList.getInstance().addBetToGlobalList(bet);
            return ResponseEntity.ok().body("Bet added!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding bet");
        }
    }
}
