package my.example.core.webapp.corewebapp.aws;

import java.util.List;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.Tag;

import org.springframework.stereotype.Service;

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

    public void putObject(PutObjectRequest putObjectRequest) {
        amazonS3.putObject(putObjectRequest);
    }

    public List<S3ObjectSummary> listObjects(String bucketName) {
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries();
    }

    public List<Tag> getObjectTagging(String bucketName, String key) {
        GetObjectTaggingRequest getObjectTaggingRequest = new GetObjectTaggingRequest(bucketName, key);
        GetObjectTaggingResult objectTagging = amazonS3.getObjectTagging(getObjectTaggingRequest);
        return objectTagging.getTagSet();
    }

    public ObjectMetadata getObjectMetadata(String bucketName, String key) {
        return amazonS3.getObjectMetadata(bucketName, key);
    }

}
