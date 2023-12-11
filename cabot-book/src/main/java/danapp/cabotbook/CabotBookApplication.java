package danapp.cabotbook;

import danapp.cabotbook.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@SpringBootApplication
public class CabotBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(CabotBookApplication.class, args);
	}

}
