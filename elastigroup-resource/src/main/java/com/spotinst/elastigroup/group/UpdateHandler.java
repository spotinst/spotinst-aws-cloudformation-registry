package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import com.amazonaws.cloudformation.proxy.*;
import com.spotinst.sdkjava.model.Elastigroup;
import com.spotinst.sdkjava.model.ElastigroupUpdateRequest;
import com.spotinst.sdkjava.model.SpotinstElastigroupClient;
import converters.ElastigroupConverter;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
                                                                       final ResourceHandlerRequest<ResourceModel> request,
                                                                       final CallbackContext callbackContext,
                                                                       final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        OperationStatus status = OperationStatus.FAILED;

        SpotinstElastigroupClient client = SpotinstClientWrapper.getSpotinstClient(model);
        Group groupToUpdate = model.getGroup();
        String groupId;

        if (groupToUpdate != null) {
            groupId = groupToUpdate.getGroupId();

            if (groupId != null) {
                ElastigroupUpdateRequest.Builder elastigroupUpdateRequestBuilder =
                        ElastigroupUpdateRequest.Builder.get();

                unsetNonChangeableFields(groupToUpdate);
                ElastigroupConverter groupConverter = new ElastigroupConverter();
                Elastigroup          apiGroupToUpdate  = groupConverter.blModelToApi(model.getGroup());

                ElastigroupUpdateRequest updateRequest       =
                        elastigroupUpdateRequestBuilder.setElastigroup(apiGroupToUpdate).build();
                Boolean                  elastigroupResponse = client.updateElastigroup(updateRequest, groupId);
                Boolean                  isUpdateSucceeded   = elastigroupResponse == true;

                if (isUpdateSucceeded) {
                    status = OperationStatus.SUCCESS;
                    String msg = String.format("Successfully updated group with id: %s", groupId);
                    logger.log(msg);
                }
                else {
                    String errorMsg = "Failed to update group";
                    logger.log(errorMsg);
                }
            }
            else {
                String errorMsg = "GroupId is null can't update group";
                logger.log(errorMsg);
            }

        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model).status(status).build();
    }

    private void unsetNonChangeableFields(Group groupToUpdate) {

        if (groupToUpdate.getCompute() != null){
            groupToUpdate.getCompute().setProduct(null);
        }

        if (groupToUpdate.getCapacity() != null){
            groupToUpdate.getCapacity().setUnit(null);
        }
    }
}
