package my.example.core.webapp.corewebapp.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;

import my.example.core.webapp.corewebapp.aws.AmazonS3Service;
import my.example.core.webapp.corewebapp.models.UploadedRelease;

@Service
public class AmazonS3Manager {

    AmazonS3Service amazonS3Service;

    public static final String VERSION_BUCKET_NAME = "aws.certf.purpose.core-webapp";
    public static final String VERSION_BUCKET_SUFFIX = "releases";
    public static final String METADATA_DESCRIPTION_NAME = "x-amz-meta-description";
    public static final String CONTENT_TYPE_JAR = "application/java-archive";

    AmazonS3Manager(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    public void uploadFile(MultipartFile multipartFile, String description) {
        String key = VERSION_BUCKET_SUFFIX + "/" + multipartFile.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(CONTENT_TYPE_JAR);
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.addUserMetadata(METADATA_DESCRIPTION_NAME, description);
        List<Tag> tagSet = new ArrayList<>();
        Tag tag = new Tag("git-tag", "");
        tagSet.add(tag);
        ObjectTagging objectTagging = new ObjectTagging(tagSet);
        PutObjectRequest putObjectRequest = new PutObjectRequest(VERSION_BUCKET_NAME, key, inputStream, objectMetadata)
                .withTagging(objectTagging);
        amazonS3Service.putObject(putObjectRequest);
    }

    public List<UploadedRelease> listObjects() {
        List<UploadedRelease> uploadedReleaseList = new ArrayList<>();
        List<S3ObjectSummary> s3ObjectSummaries = amazonS3Service.listObjects(VERSION_BUCKET_NAME);
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String key = s3ObjectSummary.getKey();
            if (!key.equals(VERSION_BUCKET_SUFFIX + "/") && key.startsWith(VERSION_BUCKET_SUFFIX)) {
                UploadedRelease uploadedRelease = new UploadedRelease();
                String version = key.replaceFirst("releases/core-webapp-", "").replaceFirst(".jar", "");
                uploadedRelease.setVersion(version);
                List<Tag> tagList = amazonS3Service.getObjectTagging(VERSION_BUCKET_NAME, key);
                tagList.forEach(tag -> uploadedRelease.setGitTag(tag.getValue()));
                uploadedRelease.setUploadTime(s3ObjectSummary.getLastModified());
                ObjectMetadata objectMetadata = amazonS3Service.getObjectMetadata(VERSION_BUCKET_NAME, key);
                Map<String, String> userMetadata = objectMetadata.getUserMetadata();
                userMetadata.forEach((metadataKey, metadataValue) -> uploadedRelease.setDescription(metadataValue));
                uploadedReleaseList.add(uploadedRelease);
            }
        }
        return uploadedReleaseList;
    }

}
