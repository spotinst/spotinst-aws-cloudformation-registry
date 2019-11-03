package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import com.amazonaws.cloudformation.proxy.*;
import com.spotinst.sdkjava.exception.SpotinstValidationException;
import com.spotinst.sdkjava.model.Elastigroup;
import com.spotinst.sdkjava.model.ElastigroupGetRequest;
import com.spotinst.sdkjava.model.SpotinstElastigroupClient;
import converters.ElastigroupConverter;


public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                       final ResourceHandlerRequest<ResourceModel> request,
                                                                       final CallbackContext callbackContext,
                                                                       final Logger logger) {

        final ResourceModel model  = request.getDesiredResourceState();
        OperationStatus     status = OperationStatus.FAILED;

        SpotinstElastigroupClient     client                       = SpotinstClientWrapper.getSpotinstClient(model);
        ElastigroupGetRequest.Builder elastigroupGetRequestBuilder = ElastigroupGetRequest.Builder.get();

        String groupId;
        Group  groupToFetch = model.getGroup();

        if (groupToFetch != null) {
            groupId = groupToFetch.getGroupId();

            ElastigroupGetRequest elastigroupGetRequest =
                    elastigroupGetRequestBuilder.setElastigroupId(groupId).build();
            Elastigroup elastigroupResponse = client.getElastigroup(elastigroupGetRequest);

            ElastigroupConverter groupConverter = new ElastigroupConverter();
            Group                blGroup        = groupConverter.apiToBlModel(elastigroupResponse);

            if (blGroup != null) {
                model.setGroup(blGroup);
                status = OperationStatus.SUCCESS;
                String msg = String.format("Successfully fetched group with id: %s", blGroup.getGroupId());
                logger.log(msg);
            }
            else {
                String errorMsg = "Failed to get group";
                logger.log(errorMsg);
            }
        }
        else{
            throw new SpotinstValidationException("Spotinst group is a required filed");
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model).status(status).build();
    }
}
