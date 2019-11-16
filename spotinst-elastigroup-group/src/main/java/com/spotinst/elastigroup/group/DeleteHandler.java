package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import software.amazon.cloudformation.proxy.*;
import com.spotinst.sdkjava.exception.SpotinstValidationException;
import com.spotinst.sdkjava.model.ElastigroupDeletionRequest;
import com.spotinst.sdkjava.model.SpotinstElastigroupClient;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                       final ResourceHandlerRequest<ResourceModel> request,
                                                                       final CallbackContext callbackContext,
                                                                       final Logger logger) {

        final ResourceModel model  = request.getDesiredResourceState();

        SpotinstElastigroupClient client = SpotinstClientWrapper.getSpotinstClient(model);

        ElastigroupDeletionRequest.Builder elastigroupDeleteRequestBuilder = ElastigroupDeletionRequest.Builder.get();

        Group groupToDelete = model.getGroup();
        if (groupToDelete != null) {
            String groupId = groupToDelete.getGroupId();

            elastigroupDeleteRequestBuilder.setElastigroupId(groupId);
            ElastigroupDeletionRequest deleteRequest = elastigroupDeleteRequestBuilder.build();
            client.deleteElastigroup(deleteRequest);

            //if got here the request succeeded - didn't throw SpotinstHttpException.
            String msg = String.format("Successfully deleted group with id: %s", groupId);
            logger.log(msg);
        }
        else{
            throw new SpotinstValidationException("Spotinst group is a required filed");
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
