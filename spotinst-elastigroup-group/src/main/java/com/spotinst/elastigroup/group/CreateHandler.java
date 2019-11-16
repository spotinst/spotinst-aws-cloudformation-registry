package com.spotinst.elastigroup.group;

import client.SpotinstClientWrapper;
import software.amazon.cloudformation.proxy.*;
import com.spotinst.sdkjava.exception.SpotinstValidationException;
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

        SpotinstElastigroupClient client = SpotinstClientWrapper.getSpotinstClient(model);

        if (model.getGroup() != null) {

            AddCreatedByCloudFormationToGroupDescription(model.getGroup());

            ElastigroupConverter groupConverter = new ElastigroupConverter();
            Elastigroup          groupToCreate  = groupConverter.blModelToApi(model.getGroup());

            ElastigroupCreationRequest.Builder elastigroupCreateRequestBuilder = ElastigroupCreationRequest.Builder.get();
            ElastigroupCreationRequest createRequest = elastigroupCreateRequestBuilder.setElastigroup(groupToCreate).build();

            Elastigroup response = client.createElastigroup(createRequest);

            //if got here the request succeeded - didn't throw SpotinstHttpException.
            model.getGroup().setGroupId(response.getId());
            String msg = String.format("Successfully created group with id: %s", response.getId());
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

    //region Private Methods
    private void AddCreatedByCloudFormationToGroupDescription(Group blGroup) {
        String groupDescription = blGroup.getDescription();

        if (groupDescription != null){
            groupDescription = groupDescription.concat(" - Created by CloudFormation Provider");
        }
        else{
            groupDescription = "Created by CloudFormation Provider";
        }

        blGroup.setDescription(groupDescription);
    }
    //endregion
}
