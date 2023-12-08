package danapp.cabotbook;

import danapp.cabotbook.Auth.AppConfig;
import danapp.cabotbook.bets.Bet;
import danapp.cabotbook.people.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CabotBookApplication {

	public static void main(String[] args) {

		User dan = new User("Dan" , 100, "10");

		Bet firstMatch  = new Bet("Ryan Mau");

		firstMatch.addBetOptionAmerican("Mau", -150.0);

		firstMatch.addBetOptionAmerican("Ryan", 125.0);


		dan.addBetToPending("Mau", firstMatch.getBetOddsDecimal("Mau"), 50);

		System.out.println(dan.getBalance());
		dan.viewPendingBets();

		dan.approvePendingBet("Mau");

		dan.viewPendingBets();
		System.out.println("Approved Bets below");

		dan.viewApprovedBets();

		dan.winPendingBet("Mau");

		System.out.println("Graded bets below");

		dan.viewGradedBets();

		System.out.println(dan.getBalance());




		System.out.println(AppConfig.getApiKey());



		SpringApplication.run(CabotBookApplication.class, args);
	}

}
