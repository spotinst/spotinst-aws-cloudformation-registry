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
public class ScalingPolicy {
    @JsonProperty("policyName")
    private String policyName;

    @JsonProperty("metricName")
    private String metricName;

    @JsonProperty("statistic")
    private String statistic;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("threshold")
    private Double threshold;

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("period")
    private Integer period;

    @JsonProperty("evaluationPeriods")
    private Integer evaluationPeriods;

    @JsonProperty("cooldown")
    private Integer cooldown;

    @JsonProperty("dimension")
    private List<Dimension> dimension;

    @JsonProperty("action")
    private Action action;

}
