// This is a generated file. Modifications will be overwritten.
package com.spotinst.elastigroup.group;

import com.amazonaws.cloudformation.Action;
import com.amazonaws.cloudformation.LambdaWrapper;
import com.amazonaws.cloudformation.metrics.MetricsPublisher;
import com.amazonaws.cloudformation.proxy.AmazonWebServicesClientProxy;
import com.amazonaws.cloudformation.proxy.CallbackAdapter;
import com.amazonaws.cloudformation.proxy.HandlerErrorCode;
import com.amazonaws.cloudformation.proxy.HandlerRequest;
import com.amazonaws.cloudformation.proxy.LoggerProxy;
import com.amazonaws.cloudformation.proxy.ProgressEvent;
import com.amazonaws.cloudformation.proxy.RequestContext;
import com.amazonaws.cloudformation.proxy.RequestData;
import com.amazonaws.cloudformation.proxy.ResourceHandlerRequest;
import com.amazonaws.cloudformation.proxy.ResourceHandlerTestPayload;
import com.amazonaws.cloudformation.resource.SchemaValidator;
import com.amazonaws.cloudformation.resource.Serializer;
import com.amazonaws.cloudformation.scheduler.CloudWatchScheduler;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;


public final class HandlerWrapper extends LambdaWrapper<ResourceModel, CallbackContext> {

    private final Configuration configuration = new Configuration();
    private final Map<Action, BaseHandler<CallbackContext>> handlers = new HashMap<>();
    private final static TypeReference<HandlerRequest<ResourceModel, CallbackContext>> REQUEST_REFERENCE =
        new TypeReference<HandlerRequest<ResourceModel, CallbackContext>>() {};
    private final static TypeReference<ResourceModel> TYPE_REFERENCE =
        new TypeReference<ResourceModel>() {};
    private final static TypeReference<ResourceHandlerTestPayload<ResourceModel, CallbackContext>> TEST_ENTRY_TYPE_REFERENCE =
        new TypeReference<ResourceHandlerTestPayload<ResourceModel, CallbackContext>>() {};


    public HandlerWrapper() {
        initialiseHandlers();
    }

    private void initialiseHandlers() {
        handlers.put(Action.CREATE, new CreateHandler());
        handlers.put(Action.DELETE, new DeleteHandler());
        handlers.put(Action.UPDATE, new UpdateHandler());
        handlers.put(Action.READ, new ReadHandler());
        handlers.put(Action.LIST, new ListHandler());
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> invokeHandler(
                final AmazonWebServicesClientProxy proxy,
                final ResourceHandlerRequest<ResourceModel> request,
                final Action action,
                final CallbackContext callbackContext) {

        final String actionName = (action == null) ? "<null>" : action.toString(); // paranoia
        if (!handlers.containsKey(action))
            throw new RuntimeException("Unknown action " + actionName);

        final BaseHandler<CallbackContext> handler = handlers.get(action);

        return handler.handleRequest(proxy, request, callbackContext, loggerProxy);
    }

    public void testEntrypoint(
            final InputStream inputStream,
            final OutputStream outputStream,
            final Context context) throws IOException {

        this.loggerProxy = new LoggerProxy();

        ProgressEvent<ResourceModel, CallbackContext> response = ProgressEvent.failed(null, null, HandlerErrorCode.InternalFailure, "Uninitialized");
        try {
            final String input = IOUtils.toString(inputStream, "UTF-8");
            final ResourceHandlerTestPayload<ResourceModel, CallbackContext> payload =
                this.serializer.deserialize(
                    input,
                    TEST_ENTRY_TYPE_REFERENCE);

            final AmazonWebServicesClientProxy proxy = new AmazonWebServicesClientProxy(
                loggerProxy, payload.getCredentials(), () -> (long) context.getRemainingTimeInMillis());

            response = invokeHandler(proxy, payload.getRequest(), payload.getAction(), payload.getCallbackContext());
        } catch (final Throwable e) {
            e.printStackTrace();
            response = ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InternalFailure);
        } finally {
            final String output = this.serializer.serialize(response);
            outputStream.write(output.toString().getBytes(Charset.forName("UTF-8")));
            outputStream.close();
        }
    }

    @Override
    public JSONObject provideResourceSchemaJSONObject() {
        return this.configuration.resourceSchemaJSONObject();
    }

    @Override
    protected ResourceHandlerRequest<ResourceModel> transform(final HandlerRequest<ResourceModel, CallbackContext> request) throws IOException {
        final RequestData<ResourceModel> requestData = request.getRequestData();

        return new ResourceHandlerRequest<ResourceModel>(
            request.getBearerToken(),
            requestData.getResourceProperties(),
            requestData.getPreviousResourceProperties(),
            requestData.getLogicalResourceId(),
            request.getNextToken()
        );
    }

    @Override
    protected TypeReference<HandlerRequest<ResourceModel, CallbackContext>> getTypeReference() {
        return REQUEST_REFERENCE;
    }

    @Override
    protected TypeReference<ResourceModel> getModelTypeReference() {
        return TYPE_REFERENCE;
    }
}
