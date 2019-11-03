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
public class Task {
    @JsonProperty("isEnabled")
    private Boolean isEnabled;

    @JsonProperty("taskType")
    private String taskType;

    @JsonProperty("cronExpression")
    private String cronExpression;

    @JsonProperty("scaleTargetCapacity")
    private Integer scaleTargetCapacity;

    @JsonProperty("scaleMinCapacity")
    private Integer scaleMinCapacity;

    @JsonProperty("scaleMaxCapacity")
    private Integer scaleMaxCapacity;

    @JsonProperty("batchSizePercentage")
    private Integer batchSizePercentage;

    @JsonProperty("gracePeriod")
    private Integer gracePeriod;

    @JsonProperty("frequency")
    private String frequency;

    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("adjustment")
    private Integer adjustment;

}
