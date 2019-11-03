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
public class AutoScale {
    @JsonProperty("isEnabled")
    private Boolean isEnabled;

    @JsonProperty("cooldown")
    private Integer cooldown;

    @JsonProperty("isAutoConfig")
    private Boolean isAutoConfig;

    @JsonProperty("shouldScaleDownNonServiceTasks")
    private Boolean shouldScaleDownNonServiceTasks;

    @JsonProperty("headroom")
    private Headroom headroom;

    @JsonProperty("down")
    private Down down;

    @JsonProperty("attributes")
    private List<Attribute> attributes;

}
