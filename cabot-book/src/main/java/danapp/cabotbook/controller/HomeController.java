package danapp.cabotbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class HomeController {
    @PostMapping("/test")
    public String home(@RequestBody String jsonData, Model model) {
        model.addAttribute("jsonData", jsonData);
        System.out.println(jsonData);
        return "home";

    }

    @GetMapping("/secured")
    public String secured() {
        return "Hello Secured";
    }
}
