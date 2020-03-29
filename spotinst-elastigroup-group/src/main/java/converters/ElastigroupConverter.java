package converters;

import com.spotinst.elastigroup.group.Tag;
import com.spotinst.elastigroup.group.*;
import com.spotinst.sdkjava.enums.AwsVolumeTypeEnum;
import com.spotinst.sdkjava.enums.ElastigroupOrientationEnum;
import com.spotinst.sdkjava.enums.SchedulingTaskTypeEnum;
import com.spotinst.sdkjava.model.BlockDeviceMapping;
import com.spotinst.sdkjava.model.IamRole;
import com.spotinst.sdkjava.model.ScalingPolicy;
import com.spotinst.sdkjava.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ElastigroupConverter {

    //region Api->Bl Model
    public Group apiToBlModel(Elastigroup apiGroup) {
        Group retVal = null;

        if (apiGroup != null) {
            retVal = new Group();

            if (apiGroup.getId() != null) {
                retVal.setGroupId(apiGroup.getId());
            }

            if (apiGroup.getName() != null) {
                retVal.setName(apiGroup.getName());
            }

            if (apiGroup.getDescription() != null) {
                retVal.setDescription(apiGroup.getDescription());
            }

            if (apiGroup.getCompute() != null) {
                convertApiToBlGroupCompute(apiGroup.getCompute(), retVal);
            }

            if (apiGroup.getCapacity() != null) {
                convertApiToBlGroupCapacity(apiGroup.getCapacity(), retVal);
            }

            if (apiGroup.getStrategy() != null) {
                convertApiToBlGroupStrategy(apiGroup.getStrategy(), retVal);
            }

            if (apiGroup.getScaling() != null) {
                convertApiToBlGroupScaling(apiGroup.getScaling(), retVal);
            }
        }

        return retVal;
    }
    //endregion

    //region Bl Model -> Api
    public Elastigroup blModelToApi(Group blGroup) {
        Elastigroup retVal = null;

        if (blGroup != null) {
            Elastigroup.Builder apiGroupBuilder = Elastigroup.Builder.get();

            if (blGroup.getName() != null) {
                apiGroupBuilder.setName(blGroup.getName());
            }

            if (blGroup.getRegion() != null) {
                apiGroupBuilder.setRegion(blGroup.getRegion());
            }

            if (blGroup.getDescription() != null) {
                apiGroupBuilder.setDescription(blGroup.getDescription());
            }

            if (blGroup.getCompute() != null) {
                convertBlToApiGroupCompute(blGroup.getCompute(), apiGroupBuilder);
            }

            if (blGroup.getCapacity() != null) {
                convertBlToApiGroupCapacity(blGroup.getCapacity(), apiGroupBuilder);
            }

            if (blGroup.getStrategy() != null) {
                convertBlToApiGroupStrategy(blGroup.getStrategy(), apiGroupBuilder);
            }

            if (blGroup.getScaling() != null) {
                convertBlToApiGroupScaling(blGroup.getScaling(), apiGroupBuilder);
            }

            if (blGroup.getThirdPartiesIntegration() != null) {

                ThirdPartiesIntegration blThirdPartiesIntegration = blGroup.getThirdPartiesIntegration();

                convertBlToApiThirdPartyIntegrations(blThirdPartiesIntegration, apiGroupBuilder);
            }

            if (blGroup.getScheduling() != null) {
                Scheduling                                 blScheduling  = blGroup.getScheduling();
                ElastigroupSchedulingConfiguration.Builder apiScheduling =
                        ElastigroupSchedulingConfiguration.Builder.get();

                if (blScheduling.getTasks() != null) {
                    List<TasksConfiguration> apiTaskConfigList = new LinkedList<>();

                    for (Task blTask : blScheduling.getTasks()) {
                        TasksConfiguration.Builder apiTaskConfig = TasksConfiguration.Builder.get();
                        apiTaskConfig.setIsEnabled(blTask.getIsEnabled());
                        apiTaskConfig.setCronExpression(blTask.getCronExpression());
                        apiTaskConfig.setTaskType(SchedulingTaskTypeEnum.fromName(blTask.getTaskType()));
                        apiTaskConfig.setBatchSizePercentage(blTask.getBatchSizePercentage());
                        apiTaskConfig.setGracePeriod(blTask.getGracePeriod());
                        apiTaskConfig.setScaleMaxCapacity(blTask.getScaleMaxCapacity());
                        apiTaskConfig.setScaleMinCapacity(blTask.getScaleMinCapacity());
                        apiTaskConfig.setScaleTargetCapacity(blTask.getScaleTargetCapacity());
                        apiTaskConfig.setAdjustment(blTask.getAdjustment());

                        if (blTask.getFrequency() != null) {
                            apiTaskConfig.setFrequency(RecurrenceFrequencyEnum.fromName(blTask.getFrequency()));
                        }

                        apiTaskConfigList.add(apiTaskConfig.build());
                    }

                    apiScheduling.setTasks(apiTaskConfigList);
                }

                apiGroupBuilder.setScheduling(apiScheduling.build());
            }

            retVal = apiGroupBuilder.build();
        }
        return retVal;
    }
    //endregion

    //region Private Methods
    //region Api to Bl
    private void convertApiToBlGroupStrategy(ElastigroupStrategyConfiguration apiStrategy, Group blGroupResult) {
        Strategy blStrategy = new Strategy();

        if (apiStrategy.getSpotPercentage() != null) {
            blStrategy.setRisk(apiStrategy.getSpotPercentage());
        }

        if (apiStrategy.getOnDemandCount() != null) {
            blStrategy.setOnDemandCount(apiStrategy.getOnDemandCount());
        }

        if (apiStrategy.getElastigroupOrientation() != null) {
            blStrategy.setAvailabilityVsCost(apiStrategy.getElastigroupOrientation().getName());
        }

        if (apiStrategy.getDrainingTimeout() != null) {
            blStrategy.setDrainingTimeout(apiStrategy.getDrainingTimeout());
        }
        if (apiStrategy.getFallbackToOd() != null) {
            blStrategy.setFallbackToOd(apiStrategy.getFallbackToOd());
        }

        // TODO: Add lifeTimePeriod, revertToSpot

        blGroupResult.setStrategy(blStrategy);

    }

    private void convertApiToBlGroupCapacity(ElastigroupCapacityConfiguration apiCapacity, Group blGroupResult) {

        Capacity blCapacity = new Capacity();

        if (apiCapacity.getMinimum() != null) {
            blCapacity.setMinimum(apiCapacity.getMinimum());
        }

        if (apiCapacity.getMaximum() != null) {
            blCapacity.setMaximum(apiCapacity.getMaximum());
        }

        if (apiCapacity.getTarget() != null) {
            blCapacity.setTarget(apiCapacity.getTarget());
        }

        if (apiCapacity.getUnit() != null) {
            blCapacity.setUnit(apiCapacity.getUnit());
        }

        blGroupResult.setCapacity(blCapacity);
    }

    private void convertApiToBlGroupCompute(ElastigroupComputeConfiguration apiCompute, Group blGroupResult) {

        Compute blModelCompute = new Compute();

        if (apiCompute.getProduct() != null) {
            blModelCompute.setProduct(apiCompute.getProduct());
        }

        //convert instance type
        if (apiCompute.getInstanceTypes() != null) {
            ElastigroupInstanceTypes apiInstanceTypes     = apiCompute.getInstanceTypes();
            InstanceTypes            blModelInstanceTypes = new InstanceTypes();

            if (apiInstanceTypes.getOnDemand() != null) {
                blModelInstanceTypes.setOnDemand(apiInstanceTypes.getOnDemand());
            }
            if (apiInstanceTypes.getSpot() != null) {
                blModelInstanceTypes.setSpot(apiInstanceTypes.getSpot());
            }

            blModelCompute.setInstanceTypes(blModelInstanceTypes);
        }

        //convert availability zones
        if (apiCompute.getAvailabilityZones() != null) {
            List<Placement>         apiAzs     = apiCompute.getAvailabilityZones();
            List<AvailabilityZones> blModelAzs = new LinkedList<>();

            for (Placement azPlacement : apiAzs) {
                AvailabilityZones blAz = new AvailabilityZones();
                blAz.setName(azPlacement.getAzName());
                blAz.setSubnetIds(azPlacement.getSubnetIds());
                blModelAzs.add(blAz);
            }

            blModelCompute.setAvailabilityZones(blModelAzs);
        }

        //convert launch specifications
        if (apiCompute.getLaunchSpecification() != null) {

            ElastigroupLaunchSpecification apiLaunchSpecification     = apiCompute.getLaunchSpecification();
            LaunchSpecification            blModelLaunchSpecification = new LaunchSpecification();

            if (apiLaunchSpecification.getDetailedMonitoring() != null) {
                blModelLaunchSpecification.setMonitoring(apiLaunchSpecification.getDetailedMonitoring());
            }

            if (apiLaunchSpecification.getEbsOptimized() != null) {
                blModelLaunchSpecification.setEbsOptimized(apiLaunchSpecification.getEbsOptimized());
            }

            if (apiLaunchSpecification.getImageId() != null) {
                blModelLaunchSpecification.setImageId(apiLaunchSpecification.getImageId());
            }

            if (apiLaunchSpecification.getKeyPair() != null) {
                blModelLaunchSpecification.setKeyPair(apiLaunchSpecification.getKeyPair());
            }

            if (apiLaunchSpecification.getUserData() != null) {
                blModelLaunchSpecification.setUserData(apiLaunchSpecification.getUserData());
            }

            if (apiLaunchSpecification.getTags() != null) {
                List<com.spotinst.sdkjava.model.Tag> apiTags = apiLaunchSpecification.getTags();
                List<Tag>                            blTags  = new LinkedList<>();

                for (com.spotinst.sdkjava.model.Tag apiTag : apiTags) {
                    Tag blTag = new Tag();
                    blTag.setTagKey(apiTag.getTagKey());
                    blTag.setTagValue(apiTag.getTagKey());

                    blTags.add(blTag);
                }
                blModelLaunchSpecification.setTags(blTags);
            }

            if (apiLaunchSpecification.getHealthCheckType() != null) {
                blModelLaunchSpecification.setHealthCheckType(apiLaunchSpecification.getHealthCheckType());
            }

            if (apiLaunchSpecification.getHealthCheckGracePeriod() != null) {
                blModelLaunchSpecification
                        .setHealthCheckGracePeriod(apiLaunchSpecification.getHealthCheckGracePeriod());
            }
            // TODO add tenancy
            blModelCompute.setLaunchSpecification(blModelLaunchSpecification);
        }

        blGroupResult.setCompute(blModelCompute);

    }

    private void convertApiToBlGroupScaling(ElastigroupScalingConfiguration apiScaling, Group blGroupResult) {
        Scaling blScaling = new Scaling();

        if (apiScaling.getUp() != null) {
            List<com.spotinst.elastigroup.group.ScalingPolicy> blUpScalingPolicies  = new LinkedList<>();
            List<ScalingPolicy>                                apiUpScalingPolicies = apiScaling.getUp();
            for (ScalingPolicy apiUpScalingPolicy : apiUpScalingPolicies) {
                com.spotinst.elastigroup.group.ScalingPolicy blScalingPolicy =
                        convertApiScalingPolicyToBl(apiUpScalingPolicy);
                blUpScalingPolicies.add(blScalingPolicy);
            }

            blScaling.setUp(blUpScalingPolicies);
        }

        if (apiScaling.getDown() != null) {
            List<com.spotinst.elastigroup.group.ScalingPolicy> blDownScalingPolicies  = new LinkedList<>();
            List<ScalingPolicy>                                apiDownScalingPolicies = apiScaling.getDown();
            for (ScalingPolicy apiDownScalingPolicy : apiDownScalingPolicies) {
                com.spotinst.elastigroup.group.ScalingPolicy blScalingPolicy =
                        convertApiScalingPolicyToBl(apiDownScalingPolicy);
                blDownScalingPolicies.add(blScalingPolicy);
            }

            blScaling.setDown(blDownScalingPolicies);
        }

        blGroupResult.setScaling(blScaling);
    }

    private void convertBlToApiThirdPartyIntegrations(ThirdPartiesIntegration blThirdPartiesIntegration,
                                                      Elastigroup.Builder apiGroupBuilder) {
        ElastigroupThirdPartiesIntegrationConfiguration.Builder apiThirdPartyBuilder =
                ElastigroupThirdPartiesIntegrationConfiguration.Builder.get();

        if (blThirdPartiesIntegration.getEcs() != null) {
            ElastigroupEcsSpecification.Builder apiEcsBuilder = ElastigroupEcsSpecification.Builder.get();
            Ecs                                 blEcs         = blThirdPartiesIntegration.getEcs();

            if (blEcs.getClusterName() != null) {
                apiEcsBuilder.setClusterName(blEcs.getClusterName());
            }

            if (blEcs.getAutoScale() != null) {
                AutoScale                                 blEcsAutoScale      = blEcs.getAutoScale();
                ElastigroupAutoScaleSpecification.Builder apiAutoScaleBuilder =
                        ElastigroupAutoScaleSpecification.Builder.get();

                if (blEcsAutoScale.getIsEnabled() != null) {
                    apiAutoScaleBuilder.setIsEnabled(blEcsAutoScale.getIsEnabled());
                }

                if (blEcsAutoScale.getCooldown() != null) {
                    apiAutoScaleBuilder.setCooldown(blEcsAutoScale.getCooldown());
                }

                if (blEcsAutoScale.getIsAutoConfig() != null) {
                    apiAutoScaleBuilder.setIsAutoConfig(blEcsAutoScale.getIsAutoConfig());
                }

                if (blEcsAutoScale.getShouldScaleDownNonServiceTasks() != null) {
                    apiAutoScaleBuilder
                            .setShouldScaleDownNonServiceTasks(blEcsAutoScale.getShouldScaleDownNonServiceTasks());
                }

                if (blEcsAutoScale.getHeadroom() != null) {
                    Headroom                                 blHeadroom  = blEcsAutoScale.getHeadroom();
                    ElastigroupHeadroomSpecification.Builder apiHeadroom =
                            ElastigroupHeadroomSpecification.Builder.get();

                    if (blHeadroom.getCpuPerUnit() != null) {
                        apiHeadroom.setCpuPerUnit(blHeadroom.getCpuPerUnit());
                    }

                    if (blHeadroom.getMemoryPerUnit() != null) {
                        apiHeadroom.setMemoryPerUnit(blHeadroom.getMemoryPerUnit());
                    }

                    if (blHeadroom.getNumOfUnits() != null) {
                        apiHeadroom.setNumOfUnits(blHeadroom.getNumOfUnits());
                    }

                    apiAutoScaleBuilder.setHeadroom(apiHeadroom.build());
                }

                if (blEcsAutoScale.getDown() != null) {
                    Down                                 blAutoScaleDown  = blEcsAutoScale.getDown();
                    ElastigroupDownSpecification.Builder apiAutoScaleDown = ElastigroupDownSpecification.Builder.get();

                    if (blAutoScaleDown.getEvaluationPeriods() != null) {
                        apiAutoScaleDown.setEvaluationPeriods(blAutoScaleDown.getEvaluationPeriods());
                    }

                    if (blAutoScaleDown.getMaxScaleDownPercentage() != null) {
                        apiAutoScaleDown.setMaxScaleDownPercentage(blAutoScaleDown.getMaxScaleDownPercentage());
                    }

                    apiAutoScaleBuilder.setDown(apiAutoScaleDown.build());
                }

                if (blEcsAutoScale.getAttributes() != null) {
                    List<ElastigroupAttributesSpecification> apiAttributesList = new LinkedList<>();

                    for (Attribute blAutoScaleAttribute : blEcsAutoScale.getAttributes()) {
                        ElastigroupAttributesSpecification.Builder apiAttribute =
                                ElastigroupAttributesSpecification.Builder.get();

                        apiAttribute.setkey(blAutoScaleAttribute.getKey());
                        apiAttribute.setValue(blAutoScaleAttribute.getKey());

                        apiAttributesList.add(apiAttribute.build());
                    }

                    apiAutoScaleBuilder.setAttributes(apiAttributesList);
                }

                apiEcsBuilder.setAutoScale(apiAutoScaleBuilder.build());
            }

            if (blEcs.getOptimizeImages() != null) {
                OptimizeImages                    blOptimizeImages   = blEcs.getOptimizeImages();
                ElastigroupOptimizeImages.Builder apiOptimizedImages = ElastigroupOptimizeImages.Builder.get();

                if (blOptimizeImages.getShouldOptimizeEcsAmi() != null) {
                    apiOptimizedImages.setShouldOptimizeEcsAmi(blOptimizeImages.getShouldOptimizeEcsAmi());
                }

                if (blOptimizeImages.getPerformAt() != null) {
                    apiOptimizedImages
                            .setPerformAt(MaintenanceWindowTypeEnum.fromName(blOptimizeImages.getPerformAt()));
                }

                if (blOptimizeImages.getTimeWindows() != null) {
                    apiOptimizedImages.setTimeWindow(blOptimizeImages.getTimeWindows());
                }

                apiEcsBuilder.setOptimizeImages(apiOptimizedImages.build());
            }

            if (blEcs.getBatch() != null) {
                ElastigroupEcsBatch.Builder apiBatch = ElastigroupEcsBatch.Builder.get();
                if (blEcs.getBatch().getJobQueueNames() != null) {
                    apiBatch.setJobQueueNames(blEcs.getBatch().getJobQueueNames());
                }

                apiEcsBuilder.setBatch(apiBatch.build());
            }

            apiThirdPartyBuilder.setEcs(apiEcsBuilder.build());
        }

        apiGroupBuilder.setThirdPartiesIntegration(apiThirdPartyBuilder.build());
    }

    private com.spotinst.elastigroup.group.ScalingPolicy convertApiScalingPolicyToBl(ScalingPolicy apiUpScalingPolicy) {
        com.spotinst.elastigroup.group.ScalingPolicy retVal = new com.spotinst.elastigroup.group.ScalingPolicy();

        if (apiUpScalingPolicy.getPolicyName() != null) {
            retVal.setPolicyName(apiUpScalingPolicy.getPolicyName());
        }

        if (apiUpScalingPolicy.getMetricName() != null) {
            retVal.setMetricName(apiUpScalingPolicy.getMetricName());
        }

        if (apiUpScalingPolicy.getStatistic() != null) {
            retVal.setStatistic(apiUpScalingPolicy.getStatistic());
        }

        if (apiUpScalingPolicy.getUnit() != null) {
            retVal.setUnit(apiUpScalingPolicy.getUnit());
        }

        if (apiUpScalingPolicy.getThreshold() != null) {
            Double threshold = new Double(apiUpScalingPolicy.getThreshold());
            retVal.setThreshold(threshold);
        }

        if (apiUpScalingPolicy.getNamespace() != null) {
            retVal.setNamespace(apiUpScalingPolicy.getNamespace());
        }

        if (apiUpScalingPolicy.getPeriod() != null) {
            retVal.setPeriod(apiUpScalingPolicy.getPeriod());
        }

        if (apiUpScalingPolicy.getEvaluationPeriods() != null) {
            retVal.setEvaluationPeriods(apiUpScalingPolicy.getEvaluationPeriods());
        }

        if (apiUpScalingPolicy.getCooldown() != null) {
            retVal.setCooldown(apiUpScalingPolicy.getCooldown());
        }

        if (apiUpScalingPolicy.getDimensions() != null) {
            List<Dimension> blScalingDimensionsList = new LinkedList<>();
            for (ScalingDimension apiDimension : apiUpScalingPolicy.getDimensions()) {
                Dimension blScalingDimension = new Dimension();
                blScalingDimension.setName(apiDimension.getName());
                blScalingDimension.setValue(apiDimension.getValue());

                blScalingDimensionsList.add(blScalingDimension);
            }

            retVal.setDimension(blScalingDimensionsList);
        }

        if (apiUpScalingPolicy.getAction() != null) {
            Action        blScalingAction  = new Action();
            ScalingAction apiScalingAction = apiUpScalingPolicy.getAction();

            blScalingAction.setAdjustment(apiScalingAction.getAdjustment());
            blScalingAction.setType(apiScalingAction.getType());
            blScalingAction.setMinTargetCapacity(apiScalingAction.getMinTargetCapacity());
            blScalingAction.setTarget(apiScalingAction.getTarget());
            blScalingAction.setMaximum(apiScalingAction.getMaximum());
            blScalingAction.setMinimum(apiScalingAction.getMinimum());

            retVal.setAction(blScalingAction);
        }

        return retVal;
    }

    //endregion

    //region Bl to Api
    private void convertBlToApiGroupStrategy(Strategy blGroupStrategy, Elastigroup.Builder groupBuilder) {
        ElastigroupStrategyConfiguration.Builder apiStrategyBuilder = ElastigroupStrategyConfiguration.Builder.get();

        if (blGroupStrategy.getRisk() != null) {
            apiStrategyBuilder.setSpotPercentage(blGroupStrategy.getRisk());
        }

        if (blGroupStrategy.getOnDemandCount() != null) {
            apiStrategyBuilder.setOnDemandCount(blGroupStrategy.getOnDemandCount());
        }

        if (blGroupStrategy.getAvailabilityVsCost() != null) {
            apiStrategyBuilder.setElastigroupOrientation(
                    ElastigroupOrientationEnum.fromName(blGroupStrategy.getAvailabilityVsCost()));
        }

        if (blGroupStrategy.getDrainingTimeout() != null) {
            apiStrategyBuilder.setDrainingTimeout(blGroupStrategy.getDrainingTimeout());
        }

        if (blGroupStrategy.getFallbackToOd() != null) {
            apiStrategyBuilder.setFallbackToOnDemand(blGroupStrategy.getFallbackToOd());
        }

        // TODO: Add lifeTimePeriod, revertToSpot

        groupBuilder.setStrategy(apiStrategyBuilder.build());
    }

    private void convertBlToApiGroupCapacity(Capacity blGroupCapacity, Elastigroup.Builder groupBuilder) {
        ElastigroupCapacityConfiguration.Builder apiGroupCapacityBuilder =
                ElastigroupCapacityConfiguration.Builder.get();


        if (blGroupCapacity.getMinimum() != null) {
            apiGroupCapacityBuilder.setMinimum(blGroupCapacity.getMinimum());
        }

        if (blGroupCapacity.getMaximum() != null) {
            apiGroupCapacityBuilder.setMaximum(blGroupCapacity.getMaximum());
        }

        if (blGroupCapacity.getTarget() != null) {
            apiGroupCapacityBuilder.setTarget(blGroupCapacity.getTarget());
        }

        if (blGroupCapacity.getUnit() != null) {
            apiGroupCapacityBuilder.setUnit(blGroupCapacity.getUnit());
        }

        groupBuilder.setCapacity(apiGroupCapacityBuilder.build());
    }

    private void convertBlToApiGroupCompute(Compute blCompute, Elastigroup.Builder groupBuilder) {
        ElastigroupComputeConfiguration.Builder apiComputeBuilder = ElastigroupComputeConfiguration.Builder.get();

        if (blCompute.getProduct() != null) {
            apiComputeBuilder.setProduct(blCompute.getProduct());
        }

        //convert instance type
        if (blCompute.getInstanceTypes() != null) {
            InstanceTypes                    blInstanceTypes             = blCompute.getInstanceTypes();
            ElastigroupInstanceTypes.Builder blModelInstanceTypesBuilder = ElastigroupInstanceTypes.Builder.get();

            if (blInstanceTypes.getOnDemand() != null) {
                blModelInstanceTypesBuilder.setOnDemandType(blInstanceTypes.getOnDemand());
            }
            if (blInstanceTypes.getSpot() != null) {
                blModelInstanceTypesBuilder.setSpotTypes(blInstanceTypes.getSpot());
            }

            apiComputeBuilder.setInstanceTypes(blModelInstanceTypesBuilder.build());
        }

        //convert availability zones
        if (blCompute.getAvailabilityZones() != null) {
            List<AvailabilityZones> blAzs  = blCompute.getAvailabilityZones();
            List<Placement>         apiAzs = new LinkedList<>();

            for (AvailabilityZones blAz : blAzs) {

                Placement.Builder apiPlacementBuilder =
                        Placement.Builder.get();// (blAz.getName(), blAz.getSubnetIds());
                apiPlacementBuilder.setAvailabilityZoneName(blAz.getName());
                apiPlacementBuilder.setSubnetIds(blAz.getSubnetIds());
                apiAzs.add(apiPlacementBuilder.build());
            }
            apiComputeBuilder.setAvailabilityZones(apiAzs);
        }

        //convert launch specifications
        if (blCompute.getLaunchSpecification() != null) {

            ElastigroupLaunchSpecification.Builder apiLaunchSpecificationBuilder =
                    ElastigroupLaunchSpecification.Builder.get();
            LaunchSpecification blModelLaunchSpecification = blCompute.getLaunchSpecification();

            if (blModelLaunchSpecification.getMonitoring() != null) {
                apiLaunchSpecificationBuilder.setDetailedMonitoring(blModelLaunchSpecification.getMonitoring());
            }

            if (blModelLaunchSpecification.getEbsOptimized() != null) {
                apiLaunchSpecificationBuilder.setEbsOptimized(blModelLaunchSpecification.getEbsOptimized());
            }

            if (blModelLaunchSpecification.getImageId() != null) {
                apiLaunchSpecificationBuilder.setImageId(blModelLaunchSpecification.getImageId());
            }

            if (blModelLaunchSpecification.getKeyPair() != null) {
                apiLaunchSpecificationBuilder.setKeyPair(blModelLaunchSpecification.getKeyPair());
            }

            if (blModelLaunchSpecification.getUserData() != null) {
                apiLaunchSpecificationBuilder.setUserData(blModelLaunchSpecification.getUserData());
            }

            if (blModelLaunchSpecification.getTags() != null) {
                List<Tag>                            blTags  = blModelLaunchSpecification.getTags();
                List<com.spotinst.sdkjava.model.Tag> apiTags = new LinkedList<>();

                for (Tag blTag : blTags) {
                    com.spotinst.sdkjava.model.Tag.Builder apiTagBuilder = com.spotinst.sdkjava.model.Tag.Builder.get();
                    apiTagBuilder.setTagKey(blTag.getTagKey());
                    apiTagBuilder.setTagValue(blTag.getTagValue());
                    apiTags.add(apiTagBuilder.build());
                }
                apiLaunchSpecificationBuilder.setTags(apiTags);
            }

            if (blModelLaunchSpecification.getHealthCheckType() != null) {
                apiLaunchSpecificationBuilder.setHealthCheckType(blModelLaunchSpecification.getHealthCheckType());
            }

            if (blModelLaunchSpecification.getHealthCheckGracePeriod() != null) {
                apiLaunchSpecificationBuilder
                        .setHealthCheckGracePeriod(blModelLaunchSpecification.getHealthCheckGracePeriod());
            }

            if (blModelLaunchSpecification.getBlockDeviceMappings() != null) {
                List<BlockDeviceMapping> apiBlockDeviceMappingsList = new LinkedList<>();

                for (com.spotinst.elastigroup.group.BlockDeviceMapping blockDeviceMapping : blModelLaunchSpecification
                        .getBlockDeviceMappings()) {
                    BlockDeviceMapping.Builder apiBlockDeviceMappingBuilder = BlockDeviceMapping.Builder.get();

                    apiBlockDeviceMappingBuilder.setDeviceName(blockDeviceMapping.getDeviceName());
                    apiBlockDeviceMappingBuilder.setNoDevice(blockDeviceMapping.getNoDevice());
                    apiBlockDeviceMappingBuilder.setVirtualName(blockDeviceMapping.getVirtualName());

                    //convert EbsDevice
                    if (blockDeviceMapping.getEbs() != null) {
                        Ebs               blEbsDevice         = blockDeviceMapping.getEbs();
                        EbsDevice.Builder apiEbsDeviceBuilder = EbsDevice.Builder.get();

                        apiEbsDeviceBuilder.setDeleteOnTermination(blEbsDevice.getDeleteOnTermination());
                        apiEbsDeviceBuilder.setEncrypted(blEbsDevice.getEncrypted());
                        apiEbsDeviceBuilder.setIops(blEbsDevice.getIops());
                        apiEbsDeviceBuilder.setSnapshotId(blEbsDevice.getSnapshotId());
                        apiEbsDeviceBuilder.setVolumeSize(blEbsDevice.getVolumeSize());
                        AwsVolumeTypeEnum volumeType = AwsVolumeTypeEnum.fromName(blEbsDevice.getVolumeType());
                        apiEbsDeviceBuilder.setVolumeType(volumeType);

                        apiBlockDeviceMappingBuilder.setEbsDevice(apiEbsDeviceBuilder.build());
                    }

                    apiBlockDeviceMappingsList.add(apiBlockDeviceMappingBuilder.build());
                }

                apiLaunchSpecificationBuilder.setBlockDeviceMappings(apiBlockDeviceMappingsList);
            }

            if (blModelLaunchSpecification.getIamRole() != null) {

                com.spotinst.elastigroup.group.IamRole blIamRole         = blModelLaunchSpecification.getIamRole();
                IamRole.Builder                        apiIamRoleBuilder = IamRole.Builder.get();
                if (blIamRole.getArn() != null) {
                    apiIamRoleBuilder.setArn(blIamRole.getArn());
                }

                if (blIamRole.getName() != null) {
                    apiIamRoleBuilder.setArn(blIamRole.getName());
                }

                apiLaunchSpecificationBuilder.setIamRole(apiIamRoleBuilder.build());
            }

            if (blModelLaunchSpecification.getSecurityGroupIds() != null) {
                List<String> blModelSecurityGroupIds = blModelLaunchSpecification.getSecurityGroupIds();
                apiLaunchSpecificationBuilder.setSecurityGroupIds(blModelSecurityGroupIds);
            }

            apiComputeBuilder.setLaunchSpecification(apiLaunchSpecificationBuilder.build());
        }

        groupBuilder.setCompute(apiComputeBuilder.build());
    }

    private void convertBlToApiGroupScaling(Scaling blScaling, Elastigroup.Builder groupBuilder) {

        ElastigroupScalingConfiguration.Builder apiScalingBuilder = ElastigroupScalingConfiguration.Builder.get();

        if (blScaling.getUp() != null) {
            List<com.spotinst.elastigroup.group.ScalingPolicy> blUpScalingPolicies  = blScaling.getUp();
            List<ScalingPolicy>                                apiUpScalingPolicies = new LinkedList<>();
            for (com.spotinst.elastigroup.group.ScalingPolicy blScalingPolicy : blUpScalingPolicies) {
                ScalingPolicy apiUpScalingPolicy = convertBlScalingPolicyToApi(blScalingPolicy);
                apiUpScalingPolicies.add(apiUpScalingPolicy);
            }

            apiScalingBuilder.setUp(apiUpScalingPolicies);
        }

        if (blScaling.getDown() != null) {
            List<com.spotinst.elastigroup.group.ScalingPolicy> blDownScalingPolicies  = blScaling.getDown();
            List<ScalingPolicy>                                apiDownScalingPolicies = new LinkedList<>();
            for (com.spotinst.elastigroup.group.ScalingPolicy blScalingPolicy : blDownScalingPolicies) {
                ScalingPolicy apiDownScalingPolicy = convertBlScalingPolicyToApi(blScalingPolicy);
                apiDownScalingPolicies.add(apiDownScalingPolicy);
            }

            apiScalingBuilder.setDown(apiDownScalingPolicies);
        }

        groupBuilder.setScaling(apiScalingBuilder.build());
    }

    private ScalingPolicy convertBlScalingPolicyToApi(com.spotinst.elastigroup.group.ScalingPolicy blScalingPolicy) {
        ScalingPolicy.Builder apiScalingPolicyBuilder = ScalingPolicy.Builder.get();
        ScalingPolicy         retVal;

        if (blScalingPolicy.getPolicyName() != null) {
            apiScalingPolicyBuilder.setPolicyName(blScalingPolicy.getPolicyName());
        }

        if (blScalingPolicy.getMetricName() != null) {
            apiScalingPolicyBuilder.setMetricName(blScalingPolicy.getMetricName());
        }

        if (blScalingPolicy.getStatistic() != null) {
            apiScalingPolicyBuilder.setStatistic(blScalingPolicy.getStatistic());
        }

        if (blScalingPolicy.getUnit() != null) {
            apiScalingPolicyBuilder.setUnit(blScalingPolicy.getUnit());
        }

        if (blScalingPolicy.getThreshold() != null) {
            Float threshold = new Float(blScalingPolicy.getThreshold());
            apiScalingPolicyBuilder.setThreshold(threshold);
        }

        if (blScalingPolicy.getNamespace() != null) {
            apiScalingPolicyBuilder.setNamespace(blScalingPolicy.getNamespace());
        }

        if (blScalingPolicy.getPeriod() != null) {
            apiScalingPolicyBuilder.setPeriod(blScalingPolicy.getPeriod());
        }

        if (blScalingPolicy.getEvaluationPeriods() != null) {
            apiScalingPolicyBuilder.setEvaluationPeriods(blScalingPolicy.getEvaluationPeriods());
        }

        if (blScalingPolicy.getCooldown() != null) {
            apiScalingPolicyBuilder.setCooldown(blScalingPolicy.getCooldown());
        }

        if (blScalingPolicy.getDimension() != null) {
            List<ScalingDimension> apiScalingDimensionList = new LinkedList<>();
            for (Dimension blDimesion : blScalingPolicy.getDimension()) {
                ScalingDimension.Builder apiScalingDimensionBuilder = ScalingDimension.Builder.get();
                apiScalingDimensionBuilder.setName(blDimesion.getName());
                apiScalingDimensionBuilder.setValue(blDimesion.getValue());

                apiScalingDimensionList.add(apiScalingDimensionBuilder.build());
            }

            apiScalingPolicyBuilder.setDimensions(apiScalingDimensionList);
        }

        if (blScalingPolicy.getAction() != null) {
            ScalingAction.Builder apiScalingAction = ScalingAction.Builder.get();
            Action                blScalingAction  = blScalingPolicy.getAction();

            apiScalingAction.setAdjustment(blScalingAction.getAdjustment());
            apiScalingAction.setType(blScalingAction.getType());
            apiScalingAction.setMinTargetCapacity(blScalingAction.getMinTargetCapacity());
            apiScalingAction.setTarget(blScalingAction.getTarget());
            apiScalingAction.setMaximum(blScalingAction.getMaximum());
            apiScalingAction.setMinimum(blScalingAction.getMinimum());

            apiScalingPolicyBuilder.setAction(apiScalingAction.build());
        }

        retVal = apiScalingPolicyBuilder.build();

        return retVal;
    }
    //endregion
    //endregion


}
