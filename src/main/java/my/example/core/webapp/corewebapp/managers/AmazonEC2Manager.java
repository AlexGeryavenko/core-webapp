package my.example.core.webapp.corewebapp.managers;

import org.springframework.stereotype.Service;

import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;

import my.example.core.webapp.corewebapp.aws.AmazonEC2Service;

@Service
public class AmazonEC2Manager {

    private final AmazonEC2Service amazonEC2Service;

    AmazonEC2Manager(AmazonEC2Service amazonEC2Service) {
        this.amazonEC2Service = amazonEC2Service;
    }

    public Instance runInstance(String version) {
        return amazonEC2Service.runInstance(version);
    }

    public void createAmi(String amiName, String instanceId) {
        amazonEC2Service.createAmi(amiName, instanceId);
    }

    public String getInstanceStateName(String instanceId) {
        DescribeInstancesResult describeInstancesResult = amazonEC2Service.getInstance(instanceId);
        return describeInstancesResult.getReservations().get(0).getInstances().get(0).getState().getName();
    }

}
