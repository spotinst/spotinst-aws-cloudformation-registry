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
public class Group {
    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("region")
    private String region;

    @JsonProperty("strategy")
    private Strategy strategy;

    @JsonProperty("compute")
    private Compute compute;

    @JsonProperty("capacity")
    private Capacity capacity;

    @JsonProperty("scaling")
    private Scaling scaling;

    @JsonProperty("thirdPartiesIntegration")
    private ThirdPartiesIntegration thirdPartiesIntegration;

    @JsonProperty("scheduling")
    private Scheduling scheduling;

}
