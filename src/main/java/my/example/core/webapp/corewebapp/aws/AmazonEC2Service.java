package my.example.core.webapp.corewebapp.aws;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.ResourceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;

@Service
public class AmazonEC2Service {

    private final AmazonEC2 amazonEC2;

    private static final String AMAZON_LINUX_2_AMI = "ami-466768ac";
    private static final String AMAZON_KEY_PAIR_NAME = "KP-certf-purpose-1";
    private static final String AMAZON_SECURITY_GROUP_NAME = "ssh-http-https-8080";
    private static final String CONTROL_APPLICATION_NAME = "control-";
    private static final String AMAZON_EC2_INSTANCE_TAG_NAME = "Name";
    private static final String IAM_INSTANCE_PROFILE_ARNS = "arn:aws:iam::656527747429:instance-profile/S3_FullAccess";

    AmazonEC2Service() {
        amazonEC2 = AmazonEC2ClientBuilder
                .standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public Instance runInstance(String version) {
        String userData = "#!/bin/bash\n" +
                "sudo yum -y install java-1.8.0\n" +
                "sudo aws s3 cp s3://aws.certf.purpose.core-webapp/releases/core-webapp-" + version + ".jar /usr/local\n" +
                "java -jar /usr/local/core-webapp-" + version + ".jar\n";

        userData = Base64.getEncoder().encodeToString(userData.getBytes(StandardCharsets.UTF_8));

        IamInstanceProfileSpecification iamInstanceProfileSpecification = new IamInstanceProfileSpecification()
                .withArn(IAM_INSTANCE_PROFILE_ARNS);

        Tag tag = new Tag(AMAZON_EC2_INSTANCE_TAG_NAME, CONTROL_APPLICATION_NAME + version);
        TagSpecification tagSpecification = new TagSpecification()
                .withTags(tag)
                .withResourceType(ResourceType.Instance);

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withImageId(AMAZON_LINUX_2_AMI)
                .withInstanceType(InstanceType.T2Micro)
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(AMAZON_KEY_PAIR_NAME)
                .withSecurityGroups(AMAZON_SECURITY_GROUP_NAME)
                .withTagSpecifications(tagSpecification)
                .withUserData(userData)
                .withIamInstanceProfile(iamInstanceProfileSpecification);
        RunInstancesResult runInstancesResult = amazonEC2.runInstances(runInstancesRequest);

        return runInstancesResult.getReservation().getInstances().get(0);
    }

    public void createAmi(String amiName, String instanceId) {
        CreateImageRequest createImageRequest = new CreateImageRequest()
                .withName(amiName)
                .withInstanceId(instanceId);
        amazonEC2.createImage(createImageRequest);
    }

    public DescribeInstancesResult getInstance(String instanceId) {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                .withInstanceIds(instanceId);
        return amazonEC2.describeInstances(describeInstancesRequest);
    }

}
