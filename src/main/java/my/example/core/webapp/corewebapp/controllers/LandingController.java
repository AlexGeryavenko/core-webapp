package my.example.core.webapp.corewebapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    @GetMapping("/")
    public String landingPage() {
        return "landing";
    }

}
