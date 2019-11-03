// This is a generated file. Modifications will be overwritten.
package com.spotinst.elastigroup.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Action {
    @JsonProperty("type")
    private String type;

    @JsonProperty("adjustment")
    private String adjustment;

    @JsonProperty("minTargetCapacity")
    private String minTargetCapacity;

    @JsonProperty("target")
    private String target;

    @JsonProperty("minimum")
    private String minimum;

    @JsonProperty("maximum")
    private String maximum;

}
