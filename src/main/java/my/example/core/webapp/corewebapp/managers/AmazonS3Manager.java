package my.example.core.webapp.corewebapp.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;

import my.example.core.webapp.corewebapp.aws.AmazonS3Service;
import my.example.core.webapp.corewebapp.models.UploadedRelease;

@Service
public class AmazonS3Manager {

    private AmazonS3Service amazonS3Service;
    private AmazonEC2Manager amazonEC2Manager;

    private static final String RELEASE_BUCKET_NAME = "aws.certf.purpose.core-webapp";
    private static final String RELEASE_BUCKET_PREFIX = "releases";
    private static final String METADATA_DESCRIPTION_NAME = "x-amz-meta-description";
    private static final String CONTENT_TYPE_JAR = "application/java-archive";
    private static final String RELEASE_GIT_TAG_NAME = "git-tag";
    private static final String AMI_CONTROL_APPLICATION_PREFIX = "ami-control-";

    AmazonS3Manager(AmazonS3Service amazonS3Service, AmazonEC2Manager amazonEC2Manager) {
        this.amazonS3Service = amazonS3Service;
        this.amazonEC2Manager = amazonEC2Manager;
    }

    public void uploadFile(MultipartFile multipartFile, String description) {
        String key = RELEASE_BUCKET_PREFIX + "/" + multipartFile.getOriginalFilename();
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
        Tag tag = new Tag(RELEASE_GIT_TAG_NAME, "");
        tagSet.add(tag);
        ObjectTagging objectTagging = new ObjectTagging(tagSet);
        PutObjectRequest putObjectRequest = new PutObjectRequest(RELEASE_BUCKET_NAME, key, inputStream, objectMetadata)
                .withTagging(objectTagging);

        // 1. Upload new release
        amazonS3Service.putObject(putObjectRequest);

        // 2. Create instance
        String version = key.replaceFirst("releases/core-webapp-", "").replaceFirst(".jar", "");
        Instance instance = amazonEC2Manager.runInstance(version);

        // 3. Create ami
        String instanceId = instance.getInstanceId();
        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String instanceStateName = amazonEC2Manager.getInstanceStateName(instanceId);
            if (InstanceStateName.Running.toString().equals(instanceStateName)) {
                amazonEC2Manager.createAmi(AMI_CONTROL_APPLICATION_PREFIX + version, instanceId);
                break;
            }
        }
    }

    public List<UploadedRelease> listObjects() {
        List<UploadedRelease> uploadedReleaseList = new ArrayList<>();
        List<S3ObjectSummary> s3ObjectSummaries = amazonS3Service.listObjects(RELEASE_BUCKET_NAME);
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String key = s3ObjectSummary.getKey();
            if (!key.equals(RELEASE_BUCKET_PREFIX + "/") && key.startsWith(RELEASE_BUCKET_PREFIX)) {
                UploadedRelease uploadedRelease = new UploadedRelease();
                String version = key.replaceFirst("releases/core-webapp-", "").replaceFirst(".jar", "");
                uploadedRelease.setVersion(version);
                List<Tag> tagList = amazonS3Service.getObjectTagging(RELEASE_BUCKET_NAME, key);
                tagList.forEach(tag -> uploadedRelease.setGitTag(tag.getValue()));
                uploadedRelease.setUploadTime(s3ObjectSummary.getLastModified());
                ObjectMetadata objectMetadata = amazonS3Service.getObjectMetadata(RELEASE_BUCKET_NAME, key);
                Map<String, String> userMetadata = objectMetadata.getUserMetadata();
                userMetadata.forEach((metadataKey, metadataValue) -> uploadedRelease.setDescription(metadataValue));
                uploadedReleaseList.add(uploadedRelease);
            }
        }
        return uploadedReleaseList;
    }

}
