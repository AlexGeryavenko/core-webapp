package my.example.core.webapp.corewebapp.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    public AmazonS3Service() {
        amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void putObject(String bucketName, MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        amazonS3.putObject(bucketName, multipartFile.getOriginalFilename(), file);
    }

}
