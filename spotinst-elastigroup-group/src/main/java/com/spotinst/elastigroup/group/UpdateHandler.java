package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import software.amazon.cloudformation.proxy.*;
import com.spotinst.sdkjava.exception.SpotinstValidationException;
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

        SpotinstElastigroupClient client        = SpotinstClientWrapper.getSpotinstClient(model);
        Group                     groupToUpdate = model.getGroup();
        String                    groupId;

        if (groupToUpdate != null) {
            groupId = groupToUpdate.getGroupId();

            if (groupId != null) {
                ElastigroupUpdateRequest.Builder elastigroupUpdateRequestBuilder =
                        ElastigroupUpdateRequest.Builder.get();

                unsetNonChangeableFields(groupToUpdate);
                ElastigroupConverter groupConverter   = new ElastigroupConverter();
                Elastigroup          apiGroupToUpdate = groupConverter.blModelToApi(model.getGroup());

                ElastigroupUpdateRequest updateRequest =
                        elastigroupUpdateRequestBuilder.setElastigroup(apiGroupToUpdate).build();
                client.updateElastigroup(updateRequest, groupId);

                //if got here the request succeeded - didn't throw SpotinstHttpException.
                String msg = String.format("Successfully updated group with id: %s", groupId);
                logger.log(msg);
            }
            else {
                throw new SpotinstValidationException("GroupId can't be null. unable to update group");
            }

        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                                                                      .status(OperationStatus.SUCCESS).build();
    }

    private void unsetNonChangeableFields(Group groupToUpdate) {
        //ignoring fields that cannot be updated
        if (groupToUpdate.getCompute() != null) {
            groupToUpdate.getCompute().setProduct(null);
        }

        if (groupToUpdate.getCapacity() != null) {
            groupToUpdate.getCapacity().setUnit(null);
        }
    }
}
