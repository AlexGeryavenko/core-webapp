package my.example.core.webapp.corewebapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import my.example.core.webapp.corewebapp.managers.AmazonS3Manager;

@Controller
public class S3Controller {

    AmazonS3Manager amazonS3Manager;

    S3Controller(AmazonS3Manager amazonS3Manager) {
        this.amazonS3Manager = amazonS3Manager;
    }

    @GetMapping("/s3/uploadFile")
    public String uploadFile() {
        return "redirect:/admin";
    }

    @PostMapping("/s3/uploadFile")
    public String uploadFile(@RequestParam(value = "file") MultipartFile file,
                             @RequestParam(value = "description", required = false, defaultValue = "defaultValue") String description) {
        amazonS3Manager.uploadFile(file, description);
        return "redirect:/admin";
    }

}
