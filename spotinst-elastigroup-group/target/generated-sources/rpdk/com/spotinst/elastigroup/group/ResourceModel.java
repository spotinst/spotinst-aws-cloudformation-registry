// This is a generated file. Modifications will be overwritten.
package com.spotinst.elastigroup.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceModel {
    @JsonIgnore
    public static final String TYPE_NAME = "Spotinst::Elastigroup::Group";

    @JsonIgnore
    public static final String IDENTIFIER_KEY_GROUP_GROUPID = "/properties/group/groupId";

    @JsonProperty("credentials")
    private Credentials credentials;

    @JsonProperty("group")
    private Group group;

    @JsonIgnore
    public JSONObject getPrimaryIdentifier() {
        final JSONObject identifier = new JSONObject();
        if (this.getGroup() != null && this.getGroup().getGroupId() != null) {
            identifier.append(IDENTIFIER_KEY_GROUP_GROUPID, this.getGroup().getGroupId());
        }

        // only return the identifier if it can be used, i.e. if all components are present
        return identifier.length() == 1 ? identifier : null;
    }

    @JsonIgnore
    public List<JSONObject> getAdditionalIdentifiers() {
        final List<JSONObject> identifiers = new ArrayList<JSONObject>();
        // only return the identifiers if any can be used
        return identifiers.isEmpty() ? null : identifiers;
    }
}
