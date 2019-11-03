// This is a generated file. Modifications will be overwritten.
package com.spotinst.elastigroup.group;

import com.amazonaws.cloudformation.proxy.AmazonWebServicesClientProxy;
import com.amazonaws.cloudformation.proxy.Logger;
import com.amazonaws.cloudformation.proxy.ProgressEvent;
import com.amazonaws.cloudformation.proxy.ResourceHandlerRequest;

public abstract class BaseHandler<CallbackT> {

    public abstract ProgressEvent<ResourceModel, CallbackT> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackT callbackContext,
        final Logger logger);

}
