package com.spotinst.elastigroup.group;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import com.spotinst.sdkjava.SpotinstClient;
import com.spotinst.sdkjava.model.ElastigroupDeletionRequest;
import com.spotinst.sdkjava.model.SpotinstElastigroupClient;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {


        throw new UnsupportedOperationException("List operation is not supported");


    }
}
