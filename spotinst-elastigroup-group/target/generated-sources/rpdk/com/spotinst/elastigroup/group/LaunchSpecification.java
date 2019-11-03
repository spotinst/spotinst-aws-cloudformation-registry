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
public class LaunchSpecification {
    @JsonProperty("securityGroupIds")
    private List<String> securityGroupIds;

    @JsonProperty("monitoring")
    private Boolean monitoring;

    @JsonProperty("ebsOptimized")
    private Boolean ebsOptimized;

    @JsonProperty("imageId")
    private String imageId;

    @JsonProperty("keyPair")
    private String keyPair;

    @JsonProperty("userData")
    private String userData;

    @JsonProperty("shutdownScript")
    private String shutdownScript;

    @JsonProperty("tags")
    private List<Tag> tags;

    @JsonProperty("healthCheckType")
    private String healthCheckType;

    @JsonProperty("healthCheckGracePeriod")
    private Integer healthCheckGracePeriod;

    @JsonProperty("tenancy")
    private String tenancy;

    @JsonProperty("blockDeviceMappings")
    private List<BlockDeviceMapping> blockDeviceMappings;

    @JsonProperty("iamRole")
    private IamRole iamRole;

}
