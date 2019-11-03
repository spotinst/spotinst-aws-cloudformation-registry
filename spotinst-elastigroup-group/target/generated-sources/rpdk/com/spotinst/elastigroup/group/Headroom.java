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
public class Headroom {
    @JsonProperty("cpuPerUnit")
    private Integer cpuPerUnit;

    @JsonProperty("memoryPerUnit")
    private Integer memoryPerUnit;

    @JsonProperty("numOfUnits")
    private Integer numOfUnits;

}
