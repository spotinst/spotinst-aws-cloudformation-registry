package com.spotinst.elastigroup.group;

import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("spotinst-elastigroup-group.json");
    }

    public JSONObject resourceSchemaJSONObject() {
        return new JSONObject(new JSONTokener(this.getClass().getClassLoader().getResourceAsStream(schemaFilename)));
    }

    /**
     * Providers should implement this method if their resource has a 'Tags' property to define resource-level tags * @return
     */
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        return null;
    }

}
