package client;

import com.spotinst.elastigroup.group.Credentials;
import com.spotinst.elastigroup.group.ResourceModel;
import com.spotinst.sdkjava.SpotinstClient;
import com.spotinst.sdkjava.exception.SpotinstValidationException;
import com.spotinst.sdkjava.model.SpotinstElastigroupClient;


public class SpotinstClientWrapper {

    public static SpotinstElastigroupClient getSpotinstClient(ResourceModel model) {
        Credentials               spotinstCredentials = model.getCredentials();
        SpotinstElastigroupClient retVal;

        if (spotinstCredentials != null) {
            String accountId = spotinstCredentials.getAccountId();

            if (accountId != null) {
                String accessToken = spotinstCredentials.getAccessToken();

                if (accessToken != null) {
                    retVal = SpotinstClient.getElastigroupClient(accessToken, accountId);
                }
                else {
                    throw new SpotinstValidationException("Spotinst access token is a required filed");
                }
            }
            else {
                throw new SpotinstValidationException("Spotinst account Id is a required field");
            }
        }
        else {
            throw new SpotinstValidationException("Spotinst Credentials is a required field in resource schema");
        }

        return retVal;
    }
}
