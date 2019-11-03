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
public class Ebs {
    @JsonProperty("deleteOnTermination")
    private Boolean deleteOnTermination;

    @JsonProperty("encrypted")
    private Boolean encrypted;

    @JsonProperty("iops")
    private Integer iops;

    @JsonProperty("snapshotId")
    private String snapshotId;

    @JsonProperty("volumeSize")
    private Integer volumeSize;

    @JsonProperty("volumeType")
    private String volumeType;

}
