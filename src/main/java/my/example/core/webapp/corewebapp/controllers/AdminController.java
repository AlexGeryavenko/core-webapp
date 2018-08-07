package my.example.core.webapp.corewebapp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import my.example.core.webapp.corewebapp.managers.AmazonS3Manager;
import my.example.core.webapp.corewebapp.models.UploadedRelease;

@Controller
public class AdminController {

    AmazonS3Manager amazonS3Manager;

    AdminController(AmazonS3Manager amazonS3Manager) {
        this.amazonS3Manager = amazonS3Manager;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<UploadedRelease> uploadedReleaseList = amazonS3Manager.listObjects();
        model.addAttribute("uploadedReleaseList", uploadedReleaseList);
        return "admin";
    }

}
