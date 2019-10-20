package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import com.amazonaws.cloudformation.proxy.*;
import com.spotinst.sdkjava.model.*;
import converters.ElastigroupConverter;



public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        OperationStatus status = OperationStatus.FAILED;

        SpotinstElastigroupClient client = SpotinstClientWrapper.getSpotinstClient(model);

        if (model.getGroup() != null) {

            AddCreatedByUluruToGroupDescription(model.getGroup());

            ElastigroupConverter groupConverter = new ElastigroupConverter();
            Elastigroup          groupToCreate  = groupConverter.blModelToApi(model.getGroup());


            ElastigroupCreationRequest.Builder elastigroupCreateRequestBuilder = ElastigroupCreationRequest.Builder.get();
            ElastigroupCreationRequest createRequest = elastigroupCreateRequestBuilder.setElastigroup(groupToCreate).build();

            Elastigroup response = client.createElastigroup(createRequest);
            Boolean isCreateSucceeded   = response != null;

            if (isCreateSucceeded) {
                status = OperationStatus.SUCCESS;
                model.getGroup().setGroupId(response.getId());
                String msg = String.format("Successfully created group with id: %s", response.getId());
                logger.log(msg);
            }
            else {
                String errorMsg = "Failed to create group";
                logger.log(errorMsg);
            }
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(status)
            .build();
    }

    //region Private Methods
    private void AddCreatedByUluruToGroupDescription(Group blGroup) {
        String groupDescription = blGroup.getDescription();

        if (groupDescription != null){
            groupDescription = groupDescription.concat(" - Created by Uluru Provider");
        }
        else{
            groupDescription = "Created by Uluru Provider";
        }

        blGroup.setDescription(groupDescription);
    }
    //endregion
}
