package com.spotinst.elastigroup.group;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();


        // Set Spotinst Credentials
        Credentials spotinstCredentials = new Credentials();
        spotinstCredentials.setAccessToken("Access_Token");
        spotinstCredentials.setAccountId("Account_Id");
        model.setCredentials(spotinstCredentials);


        Group groupToCreate = new Group();

        // Set Simple Scaling
        Scaling scaling = new Scaling();

        // Up scaling
        List<ScalingPolicy> upScalingPolicies = new LinkedList<>();
        ScalingPolicy upScalingPolicy = new ScalingPolicy();
        upScalingPolicy.setPolicyName("up Scaling Policy 1");
        upScalingPolicy.setMetricName("CPUUtilization");
        upScalingPolicy.setStatistic("average");
        upScalingPolicy.setUnit("percent");
        upScalingPolicy.setThreshold(60.0);
        upScalingPolicy.setNamespace("AWS/EC2");
        upScalingPolicy.setPeriod(300);
        upScalingPolicy.setEvaluationPeriods(3);
        upScalingPolicy.setCooldown(300);

        Action action = new Action();
        action.setType("adjustment");
        action.setAdjustment("1");
        upScalingPolicy.setAction(action);
        upScalingPolicy.setCooldown(300);

        upScalingPolicies.add(upScalingPolicy);
        scaling.setUp(upScalingPolicies);


        groupToCreate.setScaling(scaling);


        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        //        assertThat(response).isNotNull();
        //        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        //        assertThat(response.getCallbackContext()).isNull();
        //        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        //        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        //        assertThat(response.getResourceModels()).isNull();
        //        assertThat(response.getMessage()).isNull();
        //        assertThat(response.getErrorCode()).isNull();
    }
}
