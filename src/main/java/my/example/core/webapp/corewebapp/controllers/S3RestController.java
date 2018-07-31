package my.example.core.webapp.corewebapp.controllers;

import my.example.core.webapp.corewebapp.aws.AmazonS3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3/")
public class S3RestController {

    AmazonS3Service amazonS3Service;

    S3RestController(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        amazonS3Service.putObject("aws.certf.purpose.core-webapp", file);
        return "redirect:/admin";
    }

}
