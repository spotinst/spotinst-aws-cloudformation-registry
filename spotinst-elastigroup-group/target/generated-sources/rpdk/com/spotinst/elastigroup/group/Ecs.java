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
public class Ecs {
    @JsonProperty("clusterName")
    private String clusterName;

    @JsonProperty("autoScale")
    private AutoScale autoScale;

    @JsonProperty("batch")
    private Batch batch;

    @JsonProperty("optimizeImages")
    private OptimizeImages optimizeImages;

}
