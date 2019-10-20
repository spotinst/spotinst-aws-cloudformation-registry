package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import com.amazonaws.cloudformation.proxy.*;
import com.spotinst.sdkjava.SpotinstClient;
import com.spotinst.sdkjava.model.ElastigroupDeletionRequest;
import com.spotinst.sdkjava.model.SpotinstElastigroupClient;
import org.json.JSONObject;

import static com.spotinst.elastigroup.group.ResourceModel.IDENTIFIER_KEY_GROUP_GROUPID;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                       final ResourceHandlerRequest<ResourceModel> request,
                                                                       final CallbackContext callbackContext,
                                                                       final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        OperationStatus status = OperationStatus.FAILED;

        SpotinstElastigroupClient client = SpotinstClientWrapper.getSpotinstClient(model);

        ElastigroupDeletionRequest.Builder elastigroupDeleteRequestBuilder = ElastigroupDeletionRequest.Builder.get();

        Group groupToDelete = model.getGroup();
        if (groupToDelete != null) {
            String groupId = groupToDelete.getGroupId();

            elastigroupDeleteRequestBuilder.setElastigroupId(groupId);
            ElastigroupDeletionRequest deleteRequest     = elastigroupDeleteRequestBuilder.build();
            Boolean                    response          = client.deleteElastigroup(deleteRequest);
            Boolean                    isDeleteSucceeded = response == true;

            if (isDeleteSucceeded) {
                String msg = String.format("Successfully deleted group with id: %s", groupId);
                status = OperationStatus.SUCCESS;
                logger.log(msg);
            }
            else {
                String errorMsg = "Failed to delete group";
                logger.log(errorMsg);
            }
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(status)
                .build();
    }
}
