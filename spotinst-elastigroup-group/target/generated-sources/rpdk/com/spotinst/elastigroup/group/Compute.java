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
public class Compute {
    @JsonProperty("instanceTypes")
    private InstanceTypes instanceTypes;

    @JsonProperty("availabilityZones")
    private List<AvailabilityZones> availabilityZones;

    @JsonProperty("product")
    private String product;

    @JsonProperty("launchSpecification")
    private LaunchSpecification launchSpecification;

}
