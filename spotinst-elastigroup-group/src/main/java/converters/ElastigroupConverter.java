package converters;

import com.spotinst.elastigroup.group.LoadBalancersConfig;
import com.spotinst.elastigroup.group.Tag;
import com.spotinst.elastigroup.group.*;
import com.spotinst.sdkjava.enums.AwsVolumeTypeEnum;
import com.spotinst.sdkjava.enums.ElastigroupOrientationEnum;
import com.spotinst.sdkjava.enums.ScalingActionTypeEnum;
import com.spotinst.sdkjava.enums.SchedulingTaskTypeEnum;
import com.spotinst.sdkjava.model.BlockDeviceMapping;
import com.spotinst.sdkjava.model.IamRole;
import com.spotinst.sdkjava.model.ScalingPolicy;
import com.spotinst.sdkjava.model.*;
import com.spotinst.sdkjava.model.bl.elastigroup.aws.BeanstalkPlatformUpdate;
import com.spotinst.sdkjava.model.bl.elastigroup.aws.BeanstalkStrategy;
import com.spotinst.sdkjava.model.bl.elastigroup.aws.ElastigroupDeploymentPreferences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

            if (apiGroup.getRegion() != null) {
                retVal.setRegion(apiGroup.getRegion());
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

            if (apiGroup.getScheduling() != null) {
                convertApiToBlScheduling(apiGroup.getScheduling(), retVal);
            }

            if (apiGroup.getThirdPartiesIntegration() != null) {
                convertApiToBlThirdPartyIntegrations(apiGroup.getThirdPartiesIntegration(), retVal);
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

            if (blGroup.getScheduling() != null) {
                Scheduling blScheduling = blGroup.getScheduling();
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
                        apiTaskConfig.setMinCapacity(blTask.getMinCapacity());
                        apiTaskConfig.setMaxCapacity(blTask.getMaxCapacity());
                        apiTaskConfig.setTargetCapacity(blTask.getTargetCapacity());
                        apiTaskConfig.setStartTime(Date.valueOf(blTask.getStartTime()));
                        apiTaskConfig.setAdjustmentPercentage(null);//TODO: itay - check

                        if (blTask.getFrequency() != null) {
                            apiTaskConfig.setFrequency(RecurrenceFrequencyEnum.fromName(blTask.getFrequency()));
                        }

                        apiTaskConfigList.add(apiTaskConfig.build());
                    }

                    apiScheduling.setTasks(apiTaskConfigList);
                }

                apiGroupBuilder.setScheduling(apiScheduling.build());
            }


            if (blGroup.getThirdPartiesIntegration() != null) {

                ThirdPartiesIntegration blThirdPartiesIntegration = blGroup.getThirdPartiesIntegration();

                convertBlToApiThirdPartyIntegrations(blThirdPartiesIntegration, apiGroupBuilder);
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

        if (apiStrategy.getRevertToSpot() != null) {
            RevertToSpot            blRevertToSpot  = new RevertToSpot();
            ElastigroupRevertToSpot apiRevertToSpot = apiStrategy.getRevertToSpot();

            if (apiRevertToSpot.getPerformAt() != null) {
                blRevertToSpot.setPerformAt(apiRevertToSpot.getPerformAt());
            }

            if (apiRevertToSpot.getTimeWindows() != null) {
                blRevertToSpot.setTimeWindows(apiRevertToSpot.getTimeWindows());
            }

            blStrategy.setRevertToSpot(blRevertToSpot);
        }

        if (apiStrategy.getConsiderODPricing() != null) {
            //TODO: itay - check
        }

        if (apiStrategy.getPersistence() != null) {
            List<Persistence>                   blPersistences = new LinkedList<>();
            Persistence                         blPersistence  = new Persistence();
            ElastigroupPersistenceConfiguration apiPersistence = apiStrategy.getPersistence();

            if (apiPersistence.getBlockDevicesMode() != null) {
                blPersistence.setBlockDevicesMode(apiPersistence.getBlockDevicesMode());
            }

            if (apiPersistence.getShouldPersistBlockDevices() != null) {
                blPersistence.setShouldPersistBlockDevices(apiPersistence.getShouldPersistBlockDevices());
            }

            if (apiPersistence.getShouldPersistRootDevice() != null) {
                blPersistence.setShouldPersistRootDevice(apiPersistence.getShouldPersistRootDevice());
            }

            if (apiPersistence.getShouldPersistPrivateIp() != null) {
                blPersistence.setShouldPersistPrivateIp(apiPersistence.getShouldPersistPrivateIp());
            }

            blPersistences.add(blPersistence);
            blStrategy.setPersistence(blPersistences);
        }

        if (apiStrategy.getUtilizeReservedInstances() != null) {
            blStrategy.setUtilizeReservedInstances(apiStrategy.getUtilizeReservedInstances());
        }

        // TODO: Add lifeTimePeriod

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
                blAz.setPlacementGroupName(null);//TODO: itay - check
                blModelAzs.add(blAz);
            }

            blModelCompute.setAvailabilityZones(blModelAzs);
        }


        if (apiCompute.getPreferredAvailabilityZones() != null) {
            List<String> blPreferredAZs = new LinkedList<>(apiCompute.getPreferredAvailabilityZones());
            blModelCompute.setPreferredAvailabilityZones(blPreferredAZs);
        }

        //convert launch specifications
        if (apiCompute.getLaunchSpecification() != null) {

            ElastigroupLaunchSpecification apiLaunchSpecification     = apiCompute.getLaunchSpecification();
            LaunchSpecification            blModelLaunchSpecification = new LaunchSpecification();

            if (apiLaunchSpecification.getBlockDeviceMappings() != null) {
                List<BlockDeviceMapping> apiBlockDeviceMappings = apiLaunchSpecification.getBlockDeviceMappings();
                List<com.spotinst.elastigroup.group.BlockDeviceMapping> blBlockDeviceMappings = new LinkedList<>();

                for (BlockDeviceMapping apiBlockDeviceMapping : apiBlockDeviceMappings) {
                    com.spotinst.elastigroup.group.BlockDeviceMapping blBlockDeviceMapping =
                            new com.spotinst.elastigroup.group.BlockDeviceMapping();

                    if (apiBlockDeviceMapping.getDeviceName() != null) {
                        blBlockDeviceMapping.setDeviceName(apiBlockDeviceMapping.getDeviceName());
                    }

                    if (apiBlockDeviceMapping.getNoDevice() != null) {
                        blBlockDeviceMapping.setNoDevice(apiBlockDeviceMapping.getNoDevice());
                    }

                    if (apiBlockDeviceMapping.getVirtualName() != null) {
                        blBlockDeviceMapping.setVirtualName(apiBlockDeviceMapping.getVirtualName());
                    }

                    if (apiBlockDeviceMapping.getEbsDevice() != null) {
                        EbsDevice apiEbs = apiBlockDeviceMapping.getEbsDevice();
                        Ebs       blEbs  = new Ebs();

                        if (apiEbs.getEncrypted() != null) {
                            blEbs.setEncrypted(apiEbs.getEncrypted());
                        }

                        if (apiEbs.getIops() != null) {
                            blEbs.setIops(apiEbs.getIops());
                        }

                        if (apiEbs.getSnapshotId() != null) {
                            blEbs.setSnapshotId(apiEbs.getSnapshotId());
                        }

                        if (apiEbs.getVolumeSize() != null) {
                            blEbs.setVolumeSize(apiEbs.getVolumeSize());
                        }

                        if (apiEbs.getVolumeType() != null) {
                            blEbs.setVolumeType(apiEbs.getVolumeType().getName());
                        }

                        if (apiEbs.getThroughput() != null) {
                            blEbs.setThroughput(String.valueOf(apiEbs.getThroughput()));
                        }

                        if (apiEbs.getDeleteOnTermination() != null) {
                            blEbs.setDeleteOnTermination(apiEbs.getDeleteOnTermination());
                        }

                        blBlockDeviceMapping.setEbs(blEbs);
                    }

                    blBlockDeviceMappings.add(blBlockDeviceMapping);
                }

                blModelLaunchSpecification.setBlockDeviceMappings(blBlockDeviceMappings);
            }

            if (apiLaunchSpecification.getDetailedMonitoring() != null) {
                blModelLaunchSpecification.setMonitoring(apiLaunchSpecification.getDetailedMonitoring());
            }

            if (apiLaunchSpecification.getEbsOptimized() != null) {
                blModelLaunchSpecification.setEbsOptimized(apiLaunchSpecification.getEbsOptimized());
            }

            if (apiLaunchSpecification.getIamRole() != null) {
                IamRole                                apiIamRole = apiLaunchSpecification.getIamRole();
                com.spotinst.elastigroup.group.IamRole blIamRole  = new com.spotinst.elastigroup.group.IamRole();

                if (apiIamRole.getArn() != null) {
                    blIamRole.setArn(apiIamRole.getArn());
                }

                if (apiIamRole.getName() != null) {
                    blIamRole.setName(apiIamRole.getName());
                }

                blModelLaunchSpecification.setIamRole(blIamRole);
            }

            if (apiLaunchSpecification.getImageId() != null) {
                blModelLaunchSpecification.setImageId(apiLaunchSpecification.getImageId());
            }

            if (apiLaunchSpecification.getImages() != null) {
                List<Images> apiImages = apiLaunchSpecification.getImages();
                List<String> blImages  = apiImages.stream().map(Images::getId).collect(Collectors.toList());

                blModelLaunchSpecification.setImages(blImages);
            }

            if (apiLaunchSpecification.getSecurityGroupIds() != null) {
                List<String> blSecurityGroupIds = new LinkedList<>(apiLaunchSpecification.getSecurityGroupIds());
                blModelLaunchSpecification.setSecurityGroupIds(blSecurityGroupIds);
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

            if (apiLaunchSpecification.getResourceTagSpecification() != null) {
                GroupResourceTagSpecification apiResourceTags = apiLaunchSpecification.getResourceTagSpecification();
                ResourceTagSpecification      blResourceTags  = new ResourceTagSpecification();

                if (apiResourceTags.getAmis() != null) {
                    Amis                  blAmis  = new Amis();
                    GroupTagSpecification apiAmis = apiResourceTags.getAmis();

                    if (apiAmis.getShouldTag() != null) {
                        blAmis.setShouldTag(apiAmis.getShouldTag());
                    }

                    blResourceTags.setAmis(blAmis);
                }

                if (apiResourceTags.getEnis() != null) {
                    Enis                  blEnis  = new Enis();
                    GroupTagSpecification apiEnis = apiResourceTags.getEnis();

                    if (apiEnis.getShouldTag() != null) {
                        blEnis.setShouldTag(apiEnis.getShouldTag());
                    }

                    blResourceTags.setEnis(blEnis);
                }

                if (apiResourceTags.getSnapshots() != null) {
                    Snapshots             blSnapshots  = new Snapshots();
                    GroupTagSpecification apiSnapshots = apiResourceTags.getSnapshots();

                    if (apiSnapshots.getShouldTag() != null) {
                        blSnapshots.setShouldTag(apiSnapshots.getShouldTag());
                    }

                    blResourceTags.setSnapshots(blSnapshots);
                }

                if (apiResourceTags.getVolumes() != null) {
                    Volumes               blVolumes  = new Volumes();
                    GroupTagSpecification apiVolumes = apiResourceTags.getVolumes();

                    if (apiVolumes.getShouldTag() != null) {
                        blVolumes.setShouldTag(apiVolumes.getShouldTag());
                    }

                    blResourceTags.setVolumes(blVolumes);
                }

                blModelLaunchSpecification.setResourceTagSpecification(blResourceTags);
            }

            if (apiLaunchSpecification.getHealthCheckType() != null) {
                blModelLaunchSpecification.setHealthCheckType(apiLaunchSpecification.getHealthCheckType());
            }

            if (apiLaunchSpecification.getHealthCheckGracePeriod() != null) {
                blModelLaunchSpecification.setHealthCheckGracePeriod(
                        apiLaunchSpecification.getHealthCheckGracePeriod());
            }

            if (apiLaunchSpecification.getHealthCheckUnhealthyDurationBeforeReplacement() != null) {
                blModelLaunchSpecification.setHealthCheckUnhealthyDurationBeforeReplacement(
                        apiLaunchSpecification.getHealthCheckUnhealthyDurationBeforeReplacement());
            }

            if (apiLaunchSpecification.getItf() != null) {
                ElastigroupItf apiItf = apiLaunchSpecification.getItf();
                Itf            blItf  = new Itf();

                if (apiItf.getFixedTargetGroups() != null) {
                    blItf.setFixedTargetGroups(apiItf.getFixedTargetGroups());
                }

                if (apiItf.getMigrationHealthinessThreshold() != null) {
                    blItf.setMigrationHealthinessThreshold(apiItf.getMigrationHealthinessThreshold());
                }

                if (apiItf.getWeightStrategy() != null) {
                    blItf.setWeightStrategy(apiItf.getWeightStrategy());
                }

                if (apiItf.getTargetGroupConfig() != null) {
                    ElastigroupTargetGroupConfig apiTargetGroupConfig = apiItf.getTargetGroupConfig();
                    TargetGroupConfig            blTargetGroupConfig  = new TargetGroupConfig();

                    if (apiTargetGroupConfig.getTags() != null) {
                        List<Tag> blTags = new LinkedList<>();

                        for (ElastigroupTargetGroupConfigTag apiTag : apiTargetGroupConfig.getTags()) {
                            Tag blTag = new Tag();

                            if (apiTag.getTagKey() != null) {
                                blTag.setTagKey(apiTag.getTagKey());
                            }

                            if (apiTag.getTagValue() != null) {
                                blTag.setTagValue(apiTag.getTagValue());
                            }

                            blTags.add(blTag);
                        }

                        blTargetGroupConfig.setTags(blTags);
                    }


                    if (apiTargetGroupConfig.getPort() != null) {
                        blTargetGroupConfig.setPort(apiTargetGroupConfig.getPort());
                    }

                    if (apiTargetGroupConfig.getProtocol() != null) {
                        blTargetGroupConfig.setProtocol(apiTargetGroupConfig.getProtocol());
                    }

                    if (apiTargetGroupConfig.getMatcher() != null) {
                        ElastigroupMatcher apiMatcher = apiTargetGroupConfig.getMatcher();
                        Matcher            blMatcher  = new Matcher();

                        if (apiMatcher.getGrpcCode() != null) {
                            blMatcher.setGrpcCode(apiMatcher.getGrpcCode());
                        }

                        if (apiMatcher.getHttpCode() != null) {
                            blMatcher.setHttpCode(apiMatcher.getHttpCode());
                        }

                        blTargetGroupConfig.setMatcher(blMatcher);
                    }

                    if (apiTargetGroupConfig.getHealthCheckPath() != null) {
                        blTargetGroupConfig.setHealthCheckPath(apiTargetGroupConfig.getHealthCheckPath());
                    }

                    if (apiTargetGroupConfig.getHealthCheckPort() != null) {
                        blTargetGroupConfig.setHealthCheckPort(apiTargetGroupConfig.getHealthCheckPort());
                    }

                    if (apiTargetGroupConfig.getHealthCheckProtocol() != null) {
                        blTargetGroupConfig.setHealthCheckProtocol(apiTargetGroupConfig.getHealthCheckProtocol());
                    }

                    if (apiTargetGroupConfig.getHealthCheckIntervalSeconds() != null) {
                        blTargetGroupConfig.setHealthCheckIntervalSeconds(
                                apiTargetGroupConfig.getHealthCheckIntervalSeconds());
                    }

                    if (apiTargetGroupConfig.getHealthCheckTimeoutSeconds() != null) {
                        blTargetGroupConfig.setHealthCheckTimeoutSeconds(
                                apiTargetGroupConfig.getHealthCheckTimeoutSeconds());
                    }

                    if (apiTargetGroupConfig.getHealthyThresholdCount() != null) {
                        blTargetGroupConfig.setHealthyThresholdCount(apiTargetGroupConfig.getHealthyThresholdCount());
                    }

                    if (apiTargetGroupConfig.getUnhealthyThresholdCount() != null) {
                        blTargetGroupConfig.setUnhealthyThresholdCount(
                                apiTargetGroupConfig.getUnhealthyThresholdCount());
                    }

                    if (apiTargetGroupConfig.getProtocolVersion() != null) {
                        blTargetGroupConfig.setProtocolVersion(apiTargetGroupConfig.getProtocolVersion());
                    }

                    if (apiTargetGroupConfig.getVpcId() != null) {
                        blTargetGroupConfig.setVpcId(apiTargetGroupConfig.getVpcId());
                    }

                    blItf.setTargetGroupConfig(blTargetGroupConfig);
                }

                if (apiItf.getLoadBalancers() != null) {
                    List<ElastigroupItfLoadBalancer> apiLoadBalancers = apiItf.getLoadBalancers();
                    List<ItfLoadBalancer>            blLoadBalancers  = new LinkedList<>();

                    for (ElastigroupItfLoadBalancer apiLb : apiLoadBalancers) {
                        ItfLoadBalancer blLoadBalancer = new ItfLoadBalancer();

                        if (apiLb.getLoadBalancerArn() != null) {
                            blLoadBalancer.setLoadBalancerArn(apiLb.getLoadBalancerArn());
                        }

                        if (apiLb.getListenerRules() != null) {
                            List<ListenerRules> blListenerRules = new LinkedList<>();

                            for (ElastigroupListenerRule apiListenerRule : apiLb.getListenerRules()) {
                                ListenerRules blListenerRule = new ListenerRules();

                                if (apiListenerRule.getRuleArn() != null) {
                                    blListenerRule.setRuleArn(apiListenerRule.getRuleArn());
                                }

                                blListenerRules.add(blListenerRule);
                            }

                            blLoadBalancer.setListenerRules(blListenerRules);
                        }

                        blLoadBalancers.add(blLoadBalancer);
                    }

                    blItf.setLoadBalancers(blLoadBalancers);
                }

                blModelLaunchSpecification.setItf(blItf);
            }


            if (apiLaunchSpecification.getLoadBalancersConfig() != null) {
                com.spotinst.sdkjava.model.LoadBalancersConfig apiLbConfig =
                        apiLaunchSpecification.getLoadBalancersConfig();
                LoadBalancersConfig blLbConfig = new LoadBalancersConfig();

                if (apiLbConfig.getLoadBalancers() != null) {
                    List<LoadBalancer>  apiLbs = apiLbConfig.getLoadBalancers();
                    List<LoadBalancers> blLbs  = new LinkedList<>();

                    for (LoadBalancer apiLb : apiLbs) {
                        LoadBalancers blLb = new LoadBalancers();

                        if (apiLb.getName() != null) {
                            blLb.setName(apiLb.getName());
                        }

                        if (apiLb.getArn() != null) {
                            blLb.setArn(apiLb.getArn());
                        }

                        if (apiLb.getType() != null) {
                            blLb.setType(apiLb.getType().getName());
                        }

                        if (apiLb.getAutoWeight() != null) {
                            blLb.setAutoWeight(apiLb.getAutoWeight());
                        }

                        if (apiLb.getBalancerId() != null) {
                            blLb.setBalancerId(apiLb.getBalancerId());
                        }

                        if (apiLb.getAzAwareness() != null) {
                            blLb.setAzAwareness(apiLb.getAzAwareness());
                        }

                        if (apiLb.getResourceName() != null) {
                            //TODO: itay - check
                        }

                        if (apiLb.getTargetSetId() != null) {
                            blLb.setTargetSetId(apiLb.getTargetSetId());
                        }

                        blLbs.add(blLb);
                    }

                    blLbConfig.setLoadBalancers(blLbs);
                }

                blModelLaunchSpecification.setLoadBalancersConfig(blLbConfig);
            }


            if (apiLaunchSpecification.getNetworkInterfaces() != null) {
                List<NetworkInterfaces> blNetworkInterfaces = new LinkedList<>();

                for (NetworkInterface apiNetworkInterface : apiLaunchSpecification.getNetworkInterfaces()) {
                    NetworkInterfaces blNetworkInterface = new NetworkInterfaces();

                    if (apiNetworkInterface.getNetworkInterfaceId() != null) {
                        blNetworkInterface.setNetworkInterfaceId(apiNetworkInterface.getNetworkInterfaceId());
                    }

                    if (apiNetworkInterface.getDescription() != null) {
                        blNetworkInterface.setDescription(apiNetworkInterface.getDescription());
                    }

                    if (apiNetworkInterface.getDeviceIndex() != null) {
                        blNetworkInterface.setDeviceIndex(apiNetworkInterface.getDeviceIndex());
                    }

                    if (apiNetworkInterface.getDeleteOnTermination() != null) {
                        blNetworkInterface.setDeleteOnTermination(apiNetworkInterface.getDeleteOnTermination());
                    }

                    if (apiNetworkInterface.getAssociatePublicIpAddress() != null) {
                        blNetworkInterface.setAssociatePublicIpAddress(
                                apiNetworkInterface.getAssociatePublicIpAddress());
                    }

                    if (apiNetworkInterface.getPrivateIpAddress() != null) {
                        //TODO: itay - check
                    }

                    if (apiNetworkInterface.getPrivateIpAddresses() != null) {
                        List<PrivateIpAddresses> blPrivateIpAddresses = new LinkedList<>();

                        for (IpAddress apiIpAddress : apiNetworkInterface.getPrivateIpAddresses()) {
                            PrivateIpAddresses blPrivateIpAddress = new PrivateIpAddresses();
                            blPrivateIpAddress.setPrimary(apiIpAddress.getPrimary());

                            if (apiIpAddress.getPrivateIpAddress() != null) {
                                blPrivateIpAddress.setPrivateIpAddress(apiIpAddress.getPrivateIpAddress());
                            }

                            blPrivateIpAddresses.add(blPrivateIpAddress);
                        }

                        blNetworkInterface.setPrivateIpAddresses(blPrivateIpAddresses);
                    }

                    if (apiNetworkInterface.getSecondaryPrivateIpAddressCount() != null) {
                        blNetworkInterface.setSecondaryPrivateIpAddressCount(
                                apiNetworkInterface.getSecondaryPrivateIpAddressCount());
                    }

                    blNetworkInterfaces.add(blNetworkInterface);
                }

                blModelLaunchSpecification.setNetworkInterfaces(blNetworkInterfaces);
            }

            blModelCompute.setLaunchSpecification(blModelLaunchSpecification);
        }


        if (apiCompute.getElasticIps() != null) {
            //TODO: itay - check
        }

        if (apiCompute.getEbsVolumePool() != null) {
            //TODO: itay - check
        }

        blGroupResult.setCompute(blModelCompute);

    }

    private void convertApiToBlGroupScaling(ElastigroupScalingConfiguration apiScaling, Group blGroupResult) {
        Scaling blScaling = new Scaling();

        if (apiScaling.getUp() != null) {
            List<com.spotinst.elastigroup.group.ScalingUpPolicy> blUpScalingPolicies  = new LinkedList<>();
            List<ScalingPolicy>                                  apiUpScalingPolicies = apiScaling.getUp();

            for (ScalingPolicy apiUpScalingPolicy : apiUpScalingPolicies) {
                com.spotinst.elastigroup.group.ScalingUpPolicy blScalingPolicy =
                        convertApiScalingUpPolicyToBl(apiUpScalingPolicy);
                blUpScalingPolicies.add(blScalingPolicy);
            }

            blScaling.setUp(blUpScalingPolicies);
        }

        if (apiScaling.getDown() != null) {
            List<com.spotinst.elastigroup.group.ScalingDownPolicy> blDownScalingPolicies  = new LinkedList<>();
            List<ScalingPolicy>                                    apiDownScalingPolicies = apiScaling.getDown();
            for (ScalingPolicy apiDownScalingPolicy : apiDownScalingPolicies) {
                com.spotinst.elastigroup.group.ScalingDownPolicy blScalingPolicy =
                        convertApiScalingDownPolicyToBl(apiDownScalingPolicy);
                blDownScalingPolicies.add(blScalingPolicy);
            }

            blScaling.setDown(blDownScalingPolicies);
        }

        if (apiScaling.getTarget() != null) {
            List<com.spotinst.elastigroup.group.Target> blTargetScalingPolicies  = new LinkedList<>();
            List<ScalingPolicy>                         apiTargetScalingPolicies = apiScaling.getTarget();

            for (ScalingPolicy apiTargetScalingPolicy : apiTargetScalingPolicies) {
                com.spotinst.elastigroup.group.Target blScalingPolicy =
                        convertApiScalingTargetPolicyToBl(apiTargetScalingPolicy);
                blTargetScalingPolicies.add(blScalingPolicy);
            }

            blScaling.setTarget(blTargetScalingPolicies);
        }

        blGroupResult.setScaling(blScaling);
    }

    private void convertApiToBlScheduling(ElastigroupSchedulingConfiguration apiScheduling, Group blGroupResult) {
        Scheduling blScheduling = new Scheduling();

        if (apiScheduling.getTasks() != null) {
            List<com.spotinst.elastigroup.group.Task> blTasks  = new LinkedList<>();
            List<TasksConfiguration>                  apiTasks = apiScheduling.getTasks();

            for (TasksConfiguration apiTask : apiTasks) {
                com.spotinst.elastigroup.group.Task blTask = convertApiSchedulingTaskToBl(apiTask);
                blTasks.add(blTask);
            }

            blScheduling.setTasks(blTasks);
        }

        blGroupResult.setScheduling(blScheduling);
    }

    private com.spotinst.elastigroup.group.Task convertApiSchedulingTaskToBl(TasksConfiguration apiTask) {
        Task blTask = new Task();

        if (apiTask.getIsEnabled() != null) {
            blTask.setIsEnabled(apiTask.getIsEnabled());
        }

        if (apiTask.getAdjustment() != null) {
            blTask.setAdjustment(apiTask.getAdjustment());
        }

        if (apiTask.getTaskType() != null) {
            blTask.setTaskType(apiTask.getTaskType().getName());
        }

        if (apiTask.getFrequency() != null) {
            blTask.setFrequency(apiTask.getFrequency().getName());
        }

        if (apiTask.getCronExpression() != null) {
            blTask.setCronExpression(apiTask.getCronExpression());
        }

        if (apiTask.getGracePeriod() != null) {
            blTask.setGracePeriod(apiTask.getGracePeriod());
        }

        if (apiTask.getAdjustmentPercentage() != null) {
            //TODO: itay - check
        }

        if (apiTask.getBatchSizePercentage() != null) {
            blTask.setBatchSizePercentage(apiTask.getBatchSizePercentage());
        }

        if (apiTask.getMinCapacity() != null) {
            blTask.setMinCapacity(apiTask.getMinCapacity());
        }

        if (apiTask.getMaxCapacity() != null) {
            blTask.setMaxCapacity(apiTask.getMaxCapacity());
        }

        if (apiTask.getScaleMinCapacity() != null) {
            blTask.setScaleMinCapacity(apiTask.getScaleMinCapacity());
        }

        if (apiTask.getScaleMaxCapacity() != null) {
            blTask.setScaleMaxCapacity(apiTask.getScaleMaxCapacity());
        }

        if (apiTask.getTargetCapacity() != null) {
            blTask.setTargetCapacity(apiTask.getTargetCapacity());
        }

        if (apiTask.getScaleTargetCapacity() != null) {
            blTask.setScaleTargetCapacity(apiTask.getScaleTargetCapacity());
        }

        return blTask;
    }

    private com.spotinst.elastigroup.group.ScalingUpPolicy convertApiScalingUpPolicyToBl(
            ScalingPolicy apiUpScalingPolicy) {
        com.spotinst.elastigroup.group.ScalingUpPolicy retVal = new com.spotinst.elastigroup.group.ScalingUpPolicy();

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
            MinTargetCapacityAction blScalingAction  = new MinTargetCapacityAction();
            ScalingAction           apiScalingAction = apiUpScalingPolicy.getAction();

            blScalingAction.setAdjustment(apiScalingAction.getAdjustment());

            if (blScalingAction.getType() != null) {
                blScalingAction.setType(apiScalingAction.getType().getName());
            }

            blScalingAction.setMinTargetCapacity(apiScalingAction.getMinTargetCapacity());
            blScalingAction.setTarget(apiScalingAction.getTarget());
            blScalingAction.setMaximum(apiScalingAction.getMaximum());
            blScalingAction.setMinimum(apiScalingAction.getMinimum());

            retVal.setAction(blScalingAction);
        }

        if (apiUpScalingPolicy.getExtendedStatistic() != null) {
            retVal.setExtendedStatistic(apiUpScalingPolicy.getExtendedStatistic());
        }

        if (apiUpScalingPolicy.getIsEnabled() != null) {
            retVal.setIsEnabled(apiUpScalingPolicy.getIsEnabled());
        }

        if (apiUpScalingPolicy.getOperator() != null) {
           retVal.setOperator(apiUpScalingPolicy.getOperator());
        }

        if (apiUpScalingPolicy.getPredictive() != null) {
            //TODO: itay - check
        }

        if (apiUpScalingPolicy.getTarget() != null) {
            //TODO: itay - check
        }

        return retVal;
    }

    private com.spotinst.elastigroup.group.ScalingDownPolicy convertApiScalingDownPolicyToBl(
            ScalingPolicy apiUpScalingPolicy) {
        com.spotinst.elastigroup.group.ScalingDownPolicy retVal =
                new com.spotinst.elastigroup.group.ScalingDownPolicy();

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
            MinTargetCapacityAction blScalingAction  = new MinTargetCapacityAction();
            ScalingAction           apiScalingAction = apiUpScalingPolicy.getAction();

            blScalingAction.setAdjustment(apiScalingAction.getAdjustment());

            if (blScalingAction.getType() != null) {
                blScalingAction.setType(apiScalingAction.getType().getName());
            }

            blScalingAction.setMinTargetCapacity(apiScalingAction.getMinTargetCapacity());
            blScalingAction.setTarget(apiScalingAction.getTarget());
            blScalingAction.setMaximum(apiScalingAction.getMaximum());
            blScalingAction.setMinimum(apiScalingAction.getMinimum());

            retVal.setAction(blScalingAction);
        }

        if (apiUpScalingPolicy.getExtendedStatistic() != null) {
            retVal.setExtendedStatistic(apiUpScalingPolicy.getExtendedStatistic());
        }

        if (apiUpScalingPolicy.getIsEnabled() != null) {
            retVal.setIsEnabled(apiUpScalingPolicy.getIsEnabled());
        }

        if (apiUpScalingPolicy.getOperator() != null) {
            retVal.setOperator(apiUpScalingPolicy.getOperator());
        }

        if (apiUpScalingPolicy.getPredictive() != null) {
            //TODO: itay - check
        }

        if (apiUpScalingPolicy.getTarget() != null) {
            //TODO: itay - check
        }

        return retVal;
    }

    private com.spotinst.elastigroup.group.Target convertApiScalingTargetPolicyToBl(
            ScalingPolicy apiTargetScalingPolicy) {
        com.spotinst.elastigroup.group.Target retVal = new com.spotinst.elastigroup.group.Target();

        if (apiTargetScalingPolicy.getPolicyName() != null) {
            retVal.setPolicyName(apiTargetScalingPolicy.getPolicyName());
        }

        if (apiTargetScalingPolicy.getMetricName() != null) {
            retVal.setMetricName(apiTargetScalingPolicy.getMetricName());
        }

        if (apiTargetScalingPolicy.getStatistic() != null) {
            retVal.setStatistic(apiTargetScalingPolicy.getStatistic());
        }

        if (apiTargetScalingPolicy.getUnit() != null) {
            retVal.setUnit(apiTargetScalingPolicy.getUnit());
        }

        if (apiTargetScalingPolicy.getNamespace() != null) {
            retVal.setNamespace(apiTargetScalingPolicy.getNamespace());
        }

        if (apiTargetScalingPolicy.getCooldown() != null) {
            retVal.setCooldown(apiTargetScalingPolicy.getCooldown());
        }

        retVal.setSource(null);//TODO: itay - chack

        return retVal;
    }

    private void convertApiToBlThirdPartyIntegrations(
            ElastigroupThirdPartiesIntegrationConfiguration api3rdPartyIntegration, Group blGroupResult) {
        ThirdPartiesIntegration bl3rdPartIntegration = new ThirdPartiesIntegration();

        if (api3rdPartyIntegration.getCodeDeploy() != null) {
            convertApiToBlThirdPartyIntegrationCodeDeploy(api3rdPartyIntegration, bl3rdPartIntegration);
        }

        if (api3rdPartyIntegration.getEcs() != null) {
            convertApiToBlThirdPartyIntegrationEcs(api3rdPartyIntegration, bl3rdPartIntegration);
        }

        if (api3rdPartyIntegration.getElasticBeanstalk() != null) {
            convertApiToBlThirdPartyIntegrationBeanstalk(api3rdPartyIntegration, bl3rdPartIntegration);
        }

        blGroupResult.setThirdPartiesIntegration(bl3rdPartIntegration);
    }

    private void convertApiToBlThirdPartyIntegrationCodeDeploy(
            ElastigroupThirdPartiesIntegrationConfiguration api3rdPartyIntegration,
            ThirdPartiesIntegration bl3rdPartIntegration) {
        ElastigroupCodeDeploy apiCodeDeploy = api3rdPartyIntegration.getCodeDeploy();
        CodeDeploy            blCodeDeploy  = new CodeDeploy();

        if (apiCodeDeploy.getCleanUpOnFailure() != null) {
            blCodeDeploy.setCleanUpOnFailure(apiCodeDeploy.getCleanUpOnFailure());
        }

        if (apiCodeDeploy.getTerminateInstanceOnFailure() != null) {
            blCodeDeploy.setTerminateInstanceOnFailure(apiCodeDeploy.getTerminateInstanceOnFailure());
        }

        if (apiCodeDeploy.getDeploymentGroups() != null) {
            List<DeploymentGroups>           blDeploymentGroups  = new LinkedList<>();
            List<ElastigroupDeploymentGroup> apiDeploymentGroups = apiCodeDeploy.getDeploymentGroups();

            for (ElastigroupDeploymentGroup apiDeploymentGroup : apiDeploymentGroups) {
                DeploymentGroups blDeploymentGroup = new DeploymentGroups();

                if (apiDeploymentGroup.getDeploymentGroupName() != null) {
                    blDeploymentGroup.setDeploymentGroupName(apiDeploymentGroup.getDeploymentGroupName());
                }

                if (apiDeploymentGroup.getApplicationName() != null) {
                    blDeploymentGroup.setApplicationName(apiDeploymentGroup.getApplicationName());
                }

                blDeploymentGroups.add(blDeploymentGroup);
            }

            blCodeDeploy.setDeploymentGroups(blDeploymentGroups);
        }

        bl3rdPartIntegration.setCodeDeploy(blCodeDeploy);
    }

    private void convertApiToBlThirdPartyIntegrationEcs(
            ElastigroupThirdPartiesIntegrationConfiguration api3rdPartyIntegration,
            ThirdPartiesIntegration bl3rdPartIntegration) {
        ElastigroupEcsSpecification apiEcs = api3rdPartyIntegration.getEcs();
        Ecs                         blEcs  = new Ecs();

        if (apiEcs.getAutoScale() != null) {
            ElastigroupAutoScaleSpecification apiAutoScale = apiEcs.getAutoScale();
            AutoScale                         blAutoScale  = blEcs.getAutoScale();

            if (apiAutoScale.getAutoConfig() != null) {
                blAutoScale.setIsAutoConfig(apiAutoScale.getAutoConfig());
            }

            if (apiAutoScale.getAttributes() != null) {
                List<Attribute> blAttributes = new LinkedList<>();

                for (ElastigroupAttributesSpecification apiAttribute : apiAutoScale.getAttributes()) {
                    Attribute blAttribute = new Attribute();

                    if (apiAttribute.getKey() != null) {
                        blAttribute.setKey(apiAttribute.getKey());
                    }

                    if (apiAttribute.getValue() != null) {
                        blAttribute.setValue(apiAttribute.getValue());
                    }

                    blAttributes.add(blAttribute);
                }

                blAutoScale.setAttributes(blAttributes);
            }

            if (apiAutoScale.getCooldown() != null) {
                blAutoScale.setCooldown(apiAutoScale.getCooldown());
            }

            if (apiAutoScale.getHeadroom() != null) {
                Headroom                         blHeadroom  = new Headroom();
                ElastigroupHeadroomSpecification apiHeadroom = apiAutoScale.getHeadroom();

                if (apiHeadroom.getCpuPerUnit() != null) {
                    blHeadroom.setCpuPerUnit(apiHeadroom.getCpuPerUnit());
                }

                if (apiHeadroom.getNumOfUnits() != null) {
                    blHeadroom.setNumOfUnits(apiHeadroom.getNumOfUnits());
                }

                if (apiHeadroom.getMemoryPerUnit() != null) {
                    blHeadroom.setMemoryPerUnit(apiHeadroom.getMemoryPerUnit());
                }

                blAutoScale.setHeadroom(blHeadroom);
            }

            if (apiAutoScale.getDown() != null) {
                ElastigroupDownSpecification apiDown = apiAutoScale.getDown();
                AutoScaleDown                blDown  = new AutoScaleDown();

                if (apiDown.getEvaluationPeriods() != null) {
                    blDown.setEvaluationPeriods(apiDown.getEvaluationPeriods());
                }

                if (apiDown.getMaxScaleDownPercentage() != null) {
                    blDown.setMaxScaleDownPercentage(apiDown.getMaxScaleDownPercentage());
                }

                blAutoScale.setDown(blDown);
            }

            if (apiAutoScale.getShouldScaleDownNonServiceTasks() != null) {
                blAutoScale.setShouldScaleDownNonServiceTasks(apiAutoScale.getShouldScaleDownNonServiceTasks());
            }

            if (apiAutoScale.getEnabled() != null) {
                blAutoScale.setIsEnabled(apiAutoScale.getEnabled());
            }

            blEcs.setAutoScale(blAutoScale);
        }

        if (apiEcs.getBatch() != null) {
            ElastigroupEcsBatch apiBatch = apiEcs.getBatch();
            Batch               blBatch  = new Batch();

            if (apiBatch.getJobQueueNames() != null) {
                List<String> jobQueueNames = new LinkedList<>(apiBatch.getJobQueueNames());
                blBatch.setJobQueueNames(jobQueueNames);
            }

            blEcs.setBatch(blBatch);
        }

        if (apiEcs.getOptimizeImages() != null) {
            ElastigroupOptimizeImages apiOptimizeImages = apiEcs.getOptimizeImages();
            OptimizeImages            blOptimizeImages  = new OptimizeImages();

            if (apiOptimizeImages.getPerformAt() != null) {
                blOptimizeImages.setPerformAt(apiOptimizeImages.getPerformAt().getName());
            }

            if (apiOptimizeImages.getShouldOptimizeEcsAmi() != null) {
                blOptimizeImages.setShouldOptimizeEcsAmi(apiOptimizeImages.getShouldOptimizeEcsAmi());
            }

            if (apiOptimizeImages.getTimeWindows() != null) {
                blOptimizeImages.setTimeWindows(apiOptimizeImages.getTimeWindows());
            }

            blEcs.setOptimizeImages(blOptimizeImages);
        }

        if (apiEcs.getClusterName() != null) {
            blEcs.setClusterName(apiEcs.getClusterName());
        }

        bl3rdPartIntegration.setEcs(blEcs);
    }

    private void convertApiToBlThirdPartyIntegrationBeanstalk(
            ElastigroupThirdPartiesIntegrationConfiguration api3rdPartyIntegration,
            ThirdPartiesIntegration bl3rdPartIntegration) {
        com.spotinst.sdkjava.model.bl.elastigroup.aws.ElasticBeanstalk apiBeanstalk =
                api3rdPartyIntegration.getElasticBeanstalk();
        ElasticBeanstalk blBeanstalk = new ElasticBeanstalk();

        if (apiBeanstalk.getEnvironmentId() != null) {
            blBeanstalk.setEnvironmentId(apiBeanstalk.getEnvironmentId());
        }

        if (apiBeanstalk.getManagedActions() != null) {
            com.spotinst.sdkjava.model.bl.elastigroup.aws.ManagedActions apiManagedActions =
                    apiBeanstalk.getManagedActions();
            ManagedActions blManagedActions = new ManagedActions();

            if (apiManagedActions.getPlatformUpdate() != null) {
                BeanstalkPlatformUpdate apiPlatformUpdate = apiManagedActions.getPlatformUpdate();
                PlatformUpdate          blPlatformUpdate  = new PlatformUpdate();

                if (apiPlatformUpdate.getPerformAt() != null) {
                    blPlatformUpdate.setPerformAt(apiPlatformUpdate.getPerformAt());
                }

                if (apiPlatformUpdate.getUpdateLevel() != null) {
                    blPlatformUpdate.setUpdateLevel(apiPlatformUpdate.getUpdateLevel());
                }

                if (apiPlatformUpdate.getTimeWindow() != null) {
                    blPlatformUpdate.setTimeWindow(apiPlatformUpdate.getTimeWindow());
                }

                if (apiPlatformUpdate.getInstanceRefreshEnabled() != null) {
                    blPlatformUpdate.setInstanceRefreshEnabled(apiPlatformUpdate.getInstanceRefreshEnabled());
                }

                blManagedActions.setPlatformUpdate(blPlatformUpdate);
            }

            blBeanstalk.setManagedActions(blManagedActions);
        }

        if (apiBeanstalk.getDeploymentPreferences() != null) {
            ElastigroupDeploymentPreferences apiDeploymentPreferences = apiBeanstalk.getDeploymentPreferences();
            DeploymentPreferences            blDeploymentPreferences  = new DeploymentPreferences();

            if (apiDeploymentPreferences.getAutomaticRoll() != null) {
                blDeploymentPreferences.setAutomaticRoll(apiDeploymentPreferences.getAutomaticRoll());
            }

            if (apiDeploymentPreferences.getGracePeriod() != null) {
                blDeploymentPreferences.setGracePeriod(apiDeploymentPreferences.getGracePeriod());
            }

            if (apiDeploymentPreferences.getBatchSizePercentage() != null) {
                blDeploymentPreferences.setBatchSizePercentage(apiDeploymentPreferences.getBatchSizePercentage());
            }

            if (apiDeploymentPreferences.getStrategy() != null) {
                BeanstalkStrategy        apiBeanstalkStrategy = apiDeploymentPreferences.getStrategy();
                ElasticBeanstalkStrategy blBeanstalkStrategy  = new ElasticBeanstalkStrategy();

                if (apiBeanstalkStrategy.getShouldDrainInstances() != null) {
                    blBeanstalkStrategy.setShouldDrainInstances(apiBeanstalkStrategy.getShouldDrainInstances());
                }

                if (apiBeanstalkStrategy.getAction() != null) {
                    blBeanstalkStrategy.setAction(apiBeanstalkStrategy.getAction());
                }

                blDeploymentPreferences.setStrategy(blBeanstalkStrategy);
            }

            blBeanstalk.setDeploymentPreferences(blDeploymentPreferences);
        }

        bl3rdPartIntegration.setElasticBeanstalk(blBeanstalk);
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

        if (blGroupStrategy.getRevertToSpot() != null) {
            ElastigroupRevertToSpot.Builder apiRevertToSpot = ElastigroupRevertToSpot.Builder.get();
            RevertToSpot                    blRevertToSpot  = blGroupStrategy.getRevertToSpot();

            if (blRevertToSpot.getPerformAt() != null) {
                apiRevertToSpot.setPerformAt(blRevertToSpot.getPerformAt());
            }

            if (blRevertToSpot.getTimeWindows() != null) {
                apiRevertToSpot.setTimeWindows(blRevertToSpot.getTimeWindows());
            }

            apiStrategyBuilder.setRevertToSpot(apiRevertToSpot.build());
        }

        if (CollectionUtils.isNotEmpty(blGroupStrategy.getPersistence())) {
            ElastigroupPersistenceConfiguration.Builder apiPersistence =
                    ElastigroupPersistenceConfiguration.Builder.get();
            Persistence blPersistence = blGroupStrategy.getPersistence().get(0);

            if (blPersistence.getBlockDevicesMode() != null) {
                apiPersistence.setBlockDevicesMode(blPersistence.getBlockDevicesMode());
            }

            if (blPersistence.getShouldPersistBlockDevices() != null) {
                apiPersistence.setShouldPersistBlockDevices(blPersistence.getShouldPersistBlockDevices());
            }

            if (blPersistence.getShouldPersistRootDevice() != null) {
                apiPersistence.setShouldPersistRootDevice(blPersistence.getShouldPersistRootDevice());
            }

            if (blPersistence.getShouldPersistPrivateIp() != null) {
                apiPersistence.setShouldPersistPrivateIp(blPersistence.getShouldPersistPrivateIp());
            }

            apiStrategyBuilder.setPersistence(apiPersistence.build());
        }

        if (blGroupStrategy.getScalingStrategy() != null) {
            //TODO: itay - check
        }

        if (blGroupStrategy.getSignals() != null) {
            //TODO: itay - check
        }

        if (blGroupStrategy.getLifetimePeriod() != null) {
            //TODO: itay - check
        }

        if (blGroupStrategy.getUtilizeCommitments() != null) {
            //TODO: itay - check
        }

        if (blGroupStrategy.getUtilizeReservedInstances() != null) {
            apiStrategyBuilder.setUtilizeReservedInstances(blGroupStrategy.getUtilizeReservedInstances());
        }

        // TODO: Add lifeTimePeriod

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

        if (blCompute.getPreferredAvailabilityZones() != null) {
            apiComputeBuilder.setPreferredAvailabilityZones(blCompute.getPreferredAvailabilityZones());
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
                apiLaunchSpecificationBuilder.setHealthCheckGracePeriod(
                        blModelLaunchSpecification.getHealthCheckGracePeriod());
            }

            if (blModelLaunchSpecification.getBlockDeviceMappings() != null) {
                List<BlockDeviceMapping> apiBlockDeviceMappingsList = new LinkedList<>();

                for (com.spotinst.elastigroup.group.BlockDeviceMapping blockDeviceMapping : blModelLaunchSpecification.getBlockDeviceMappings()) {
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

            if(blModelLaunchSpecification.getTenancy() != null){
                //TODO: itay - check
            }

            if(blModelLaunchSpecification.getNetworkInterfaces() != null){
                List<NetworkInterface> apiNetworkInterfaces = new LinkedList<>();

                for(NetworkInterfaces blNetworkInterface : blModelLaunchSpecification.getNetworkInterfaces()){
                    NetworkInterface.Builder apiNetworkInterface = NetworkInterface.Builder.get();

                    if(blNetworkInterface.getNetworkInterfaceId() != null){
                        apiNetworkInterface.setNetworkInterfaceId(blNetworkInterface.getNetworkInterfaceId());
                    }

                    if(blNetworkInterface.getDeviceIndex() != null){
                        apiNetworkInterface.setDeviceIndex(blNetworkInterface.getDeviceIndex());
                    }

                    if(blNetworkInterface.getDescription() != null){
                        apiNetworkInterface.setDescription(blNetworkInterface.getDescription());
                    }

                    if(blNetworkInterface.getSubnetId() != null){
                        //TODO: itay - check
                    }

                    if(blNetworkInterface.getDeleteOnTermination() != null){
                        apiNetworkInterface.setDeleteOnTermination(blNetworkInterface.getDeleteOnTermination());
                    }

                    if(blNetworkInterface.getAssociatePublicIpAddress() != null){
                        apiNetworkInterface.setAssociatePublicIpAddress(blNetworkInterface.getAssociatePublicIpAddress());
                    }

                    if(blNetworkInterface.getSecondaryPrivateIpAddressCount() != null){
                        apiNetworkInterface.setSecondaryPrivateIpAddressCount(blNetworkInterface.getSecondaryPrivateIpAddressCount());
                    }

                    if(blNetworkInterface.getAssociateIpv6Address() != null){
                        //TODO: itay - check
                    }

                    if(blNetworkInterface.getPrivateIpAddresses() != null){
                        List<IpAddress> apiIps = new LinkedList<>();

                        for(PrivateIpAddresses blIp : blNetworkInterface.getPrivateIpAddresses()){
                            IpAddress.Builder apiIp = IpAddress.Builder.get();

                            if(blIp.getPrimary() != null){
                                apiIp.setPrimary(blIp.getPrimary());
                            }

                            if(blIp.getPrivateIpAddress() != null){
                                apiIp.setPrivateIpAddress(blIp.getPrivateIpAddress());
                            }

                            apiIps.add(apiIp.build());
                        }

                        apiNetworkInterface.setPrivateIpAddresses(apiIps);
                    }

                    apiNetworkInterfaces.add(apiNetworkInterface.build());
                }

                apiLaunchSpecificationBuilder.setNetworkInterfaces(apiNetworkInterfaces);
            }

            apiComputeBuilder.setLaunchSpecification(apiLaunchSpecificationBuilder.build());
        }

        if (blCompute.getSubnetIds() != null) {
            //TODO: itay - check
        }

        if (blCompute.getPrivateIps() != null) {
            //TODO: itay - check
        }

        if (blCompute.getVolumeAttachments() != null) {
            //TODO: itay - check
        }

        groupBuilder.setCompute(apiComputeBuilder.build());
    }

    private void convertBlToApiGroupScaling(Scaling blScaling, Elastigroup.Builder groupBuilder) {

        ElastigroupScalingConfiguration.Builder apiScalingBuilder = ElastigroupScalingConfiguration.Builder.get();

        if (blScaling.getUp() != null) {
            List<com.spotinst.elastigroup.group.ScalingUpPolicy> blUpScalingPolicies  = blScaling.getUp();
            List<ScalingPolicy>                                  apiUpScalingPolicies = new LinkedList<>();
            for (com.spotinst.elastigroup.group.ScalingUpPolicy blScalingPolicy : blUpScalingPolicies) {
                ScalingPolicy apiUpScalingPolicy = convertBlScalingUpPolicyToApi(blScalingPolicy);
                apiUpScalingPolicies.add(apiUpScalingPolicy);
            }

            apiScalingBuilder.setUp(apiUpScalingPolicies);
        }

        if (blScaling.getDown() != null) {
            List<com.spotinst.elastigroup.group.ScalingDownPolicy> blDownScalingPolicies  = blScaling.getDown();
            List<ScalingPolicy>                                    apiDownScalingPolicies = new LinkedList<>();
            for (com.spotinst.elastigroup.group.ScalingDownPolicy blScalingPolicy : blDownScalingPolicies) {
                ScalingPolicy apiDownScalingPolicy = convertBlScalingDownPolicyToApi(blScalingPolicy);
                apiDownScalingPolicies.add(apiDownScalingPolicy);
            }

            apiScalingBuilder.setDown(apiDownScalingPolicies);
        }

        if (blScaling.getTarget() != null) {
            List<com.spotinst.elastigroup.group.Target> blTargetScalingPolicies  = blScaling.getTarget();
            List<ScalingPolicy>                         apiTargetScalingPolicies = new LinkedList<>();
            for (com.spotinst.elastigroup.group.Target blScalingPolicy : blTargetScalingPolicies) {
                ScalingPolicy apiTargetScalingPolicy = convertBlScalingTargetPolicyToApi(blScalingPolicy);
                apiTargetScalingPolicies.add(apiTargetScalingPolicy);
            }

            apiScalingBuilder.setTarget(apiTargetScalingPolicies);
        }

        if (blScaling.getMultipleMetrics() != null) {
            //TODO: itay - check
        }

        groupBuilder.setScaling(apiScalingBuilder.build());
    }

    private ScalingPolicy convertBlScalingUpPolicyToApi(
            com.spotinst.elastigroup.group.ScalingUpPolicy blScalingPolicy) {
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
            ScalingAction.Builder   apiScalingAction = ScalingAction.Builder.get();
            MinTargetCapacityAction blScalingAction  = blScalingPolicy.getAction();

            apiScalingAction.setAdjustment(blScalingAction.getAdjustment());
            apiScalingAction.setType(ScalingActionTypeEnum.fromName(blScalingAction.getType()));
            apiScalingAction.setMinTargetCapacity(blScalingAction.getMinTargetCapacity());
            apiScalingAction.setTarget(blScalingAction.getTarget());
            apiScalingAction.setMaximum(blScalingAction.getMaximum());
            apiScalingAction.setMinimum(blScalingAction.getMinimum());

            apiScalingPolicyBuilder.setAction(apiScalingAction.build());
        }

        if (blScalingPolicy.getIsEnabled() != null) {
            apiScalingPolicyBuilder.setIsEnabled(blScalingPolicy.getIsEnabled());
        }

        if (blScalingPolicy.getExtendedStatistic() != null) {
            apiScalingPolicyBuilder.setExtendedStatistic(blScalingPolicy.getExtendedStatistic());
        }

        if (blScalingPolicy.getOperator() != null) {
            apiScalingPolicyBuilder.setOperator(blScalingPolicy.getOperator());
        }

        if (blScalingPolicy.getStepAdjustments() != null) {
            //TODO: itay - check
        }

        retVal = apiScalingPolicyBuilder.build();

        return retVal;
    }

    private ScalingPolicy convertBlScalingDownPolicyToApi(
            com.spotinst.elastigroup.group.ScalingDownPolicy blScalingPolicy) {
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
            ScalingAction.Builder   apiScalingAction = ScalingAction.Builder.get();
            MinTargetCapacityAction blScalingAction  = blScalingPolicy.getAction();

            apiScalingAction.setAdjustment(blScalingAction.getAdjustment());
            apiScalingAction.setType(ScalingActionTypeEnum.fromName(blScalingAction.getType()));
            apiScalingAction.setMinTargetCapacity(blScalingAction.getMinTargetCapacity());
            apiScalingAction.setTarget(blScalingAction.getTarget());
            apiScalingAction.setMaximum(blScalingAction.getMaximum());
            apiScalingAction.setMinimum(blScalingAction.getMinimum());

            apiScalingPolicyBuilder.setAction(apiScalingAction.build());
        }

        if (blScalingPolicy.getOperator() != null) {
            apiScalingPolicyBuilder.setOperator(blScalingPolicy.getOperator());
        }

        if (blScalingPolicy.getExtendedStatistic() != null) {
            apiScalingPolicyBuilder.setExtendedStatistic(blScalingPolicy.getExtendedStatistic());
        }

        if (blScalingPolicy.getIsEnabled() != null) {
            apiScalingPolicyBuilder.setIsEnabled(blScalingPolicy.getIsEnabled());
        }

        if (blScalingPolicy.getStepAdjustments() != null) {
            //TODO: itay- check
        }

        retVal = apiScalingPolicyBuilder.build();

        return retVal;
    }

    private ScalingPolicy convertBlScalingTargetPolicyToApi(com.spotinst.elastigroup.group.Target blScalingPolicy) {
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

        if (blScalingPolicy.getNamespace() != null) {
            apiScalingPolicyBuilder.setNamespace(blScalingPolicy.getNamespace());
        }

        if (blScalingPolicy.getCooldown() != null) {
            apiScalingPolicyBuilder.setCooldown(blScalingPolicy.getCooldown());
        }

        retVal = apiScalingPolicyBuilder.build();

        return retVal;
    }

    private void convertBlToApiThirdPartyIntegrations(ThirdPartiesIntegration blThirdPartiesIntegration,
                                                      Elastigroup.Builder apiGroupBuilder) {
        ElastigroupThirdPartiesIntegrationConfiguration.Builder apiThirdPartyBuilder =
                ElastigroupThirdPartiesIntegrationConfiguration.Builder.get();

        if (blThirdPartiesIntegration.getChef() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getCodeDeploy() != null) {
            ElastigroupCodeDeploy.Builder apiCodeDeploy = ElastigroupCodeDeploy.Builder.get();
            CodeDeploy                    blCodeDeploy  = blThirdPartiesIntegration.getCodeDeploy();

            if (blCodeDeploy.getCleanUpOnFailure() != null) {
                apiCodeDeploy.setCleanUpOnFailure(blCodeDeploy.getCleanUpOnFailure());
            }

            if (blCodeDeploy.getDeploymentGroups() != null) {
                List<DeploymentGroups>           blDeploymentGroups  = blCodeDeploy.getDeploymentGroups();
                List<ElastigroupDeploymentGroup> apiDeploymentGroups = new LinkedList<>();

                for (DeploymentGroups blDeploymentGroup : blDeploymentGroups) {
                    ElastigroupDeploymentGroup.Builder apiDeploymentGroup = ElastigroupDeploymentGroup.Builder.get();

                    if (blDeploymentGroup.getDeploymentGroupName() != null) {
                        apiDeploymentGroup.setDeploymentGroupName(blDeploymentGroup.getDeploymentGroupName());
                    }

                    if (blDeploymentGroup.getApplicationName() != null) {
                        apiDeploymentGroup.setApplicationName(blDeploymentGroup.getApplicationName());
                    }

                    apiDeploymentGroups.add(apiDeploymentGroup.build());
                }

                apiCodeDeploy.setDeploymentGroups(apiDeploymentGroups);
            }

            if (blCodeDeploy.getTerminateInstanceOnFailure() != null) {
                apiCodeDeploy.setTerminateInstanceOnFailure(blCodeDeploy.getTerminateInstanceOnFailure());
            }

            apiThirdPartyBuilder.setCodeDeploy(apiCodeDeploy.build());
        }

        if (blThirdPartiesIntegration.getDockerSwarm() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getEcs() != null) {
            ElastigroupEcsSpecification.Builder apiEcsBuilder = ElastigroupEcsSpecification.Builder.get();
            Ecs                                 blEcs         = blThirdPartiesIntegration.getEcs();

            if (blEcs.getClusterName() != null) {
                apiEcsBuilder.setClusterName(blEcs.getClusterName());
            }

            if (blEcs.getAutoScale() != null) {
                AutoScale blEcsAutoScale = blEcs.getAutoScale();
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
                    apiAutoScaleBuilder.setShouldScaleDownNonServiceTasks(
                            blEcsAutoScale.getShouldScaleDownNonServiceTasks());
                }

                if (blEcsAutoScale.getHeadroom() != null) {
                    Headroom blHeadroom = blEcsAutoScale.getHeadroom();
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
                    AutoScaleDown                        blAutoScaleDown  = blEcsAutoScale.getDown();
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

                if (blEcsAutoScale.getEnableAutomaticAndManualHeadroom() != null) {
                    //TODO: itay - check
                }

                if (blEcsAutoScale.getResourceLimits() != null) {
                    //TODO: itay - check
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
                    apiOptimizedImages.setPerformAt(
                            MaintenanceWindowTypeEnum.fromName(blOptimizeImages.getPerformAt()));
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

        if (blThirdPartiesIntegration.getElasticBeanstalk() != null) {
            ElasticBeanstalk blElasticBeanstalk = blThirdPartiesIntegration.getElasticBeanstalk();
            com.spotinst.sdkjava.model.bl.elastigroup.aws.ElasticBeanstalk.Builder apiElasticBeanstalk =
                    com.spotinst.sdkjava.model.bl.elastigroup.aws.ElasticBeanstalk.Builder.get();

            if (blElasticBeanstalk.getEnvironmentId() != null) {
                apiElasticBeanstalk.setEnvironmentId(blElasticBeanstalk.getEnvironmentId());
            }

            if (blElasticBeanstalk.getManagedActions() != null) {
                ManagedActions blManagedActions = blElasticBeanstalk.getManagedActions();
                com.spotinst.sdkjava.model.bl.elastigroup.aws.ManagedActions.Builder apiManagedActions =
                        com.spotinst.sdkjava.model.bl.elastigroup.aws.ManagedActions.Builder.get();

                if (blManagedActions.getPlatformUpdate() != null) {
                    BeanstalkPlatformUpdate.Builder apiPlatformUpdate = BeanstalkPlatformUpdate.Builder.get();
                    PlatformUpdate                  blPlatformUpdate  = blManagedActions.getPlatformUpdate();

                    if (blPlatformUpdate.getPerformAt() != null) {
                        apiPlatformUpdate.setPerformAt(blPlatformUpdate.getPerformAt());
                    }

                    if (blPlatformUpdate.getUpdateLevel() != null) {
                        apiPlatformUpdate.setUpdateLevel(blPlatformUpdate.getUpdateLevel());
                    }

                    if (blPlatformUpdate.getTimeWindow() != null) {
                        apiPlatformUpdate.setTimeWindow(blPlatformUpdate.getTimeWindow());
                    }

                    if (blPlatformUpdate.getInstanceRefreshEnabled() != null) {
                        apiPlatformUpdate.setInstanceRefreshEnabled(blPlatformUpdate.getInstanceRefreshEnabled());
                    }

                    apiManagedActions.setPlatformUpdate(apiPlatformUpdate.build());
                }

                apiElasticBeanstalk.setManagedActions(apiManagedActions.build());
            }

            if (blElasticBeanstalk.getDeploymentPreferences() != null) {
                ElastigroupDeploymentPreferences.Builder apiDeploymentPreferences =
                        ElastigroupDeploymentPreferences.Builder.get();
                DeploymentPreferences blDeploymentPreferences = blElasticBeanstalk.getDeploymentPreferences();

                if (blDeploymentPreferences.getAutomaticRoll() != null) {
                    apiDeploymentPreferences.setAutomaticRoll(blDeploymentPreferences.getAutomaticRoll());
                }

                if (blDeploymentPreferences.getGracePeriod() != null) {
                    apiDeploymentPreferences.setGracePeriod(blDeploymentPreferences.getGracePeriod());
                }

                if (blDeploymentPreferences.getStrategy() != null) {
                    BeanstalkStrategy.Builder apiBeanstalkStrategy = BeanstalkStrategy.Builder.get();
                    ElasticBeanstalkStrategy  blBeanstalkStrategy  = blDeploymentPreferences.getStrategy();

                    if (blBeanstalkStrategy.getShouldDrainInstances() != null) {
                        apiBeanstalkStrategy.setShouldDrainInstances(blBeanstalkStrategy.getShouldDrainInstances());
                    }

                    if (blBeanstalkStrategy.getAction() != null) {
                        apiBeanstalkStrategy.setAction(blBeanstalkStrategy.getAction());
                    }

                    apiDeploymentPreferences.setStrategy(apiBeanstalkStrategy.build());
                }

                if (blDeploymentPreferences.getBatchSizePercentage() != null) {
                    apiDeploymentPreferences.setBatchSizePercentage(blDeploymentPreferences.getBatchSizePercentage());
                }

                apiElasticBeanstalk.setDeploymentPreferences(apiDeploymentPreferences.build());
            }

            apiThirdPartyBuilder.setElasticBeanstalk(apiElasticBeanstalk.build());
        }

        if (blThirdPartiesIntegration.getJenkins() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getKubernetes() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getMesosphere() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getMlbRuntime() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getNomad() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getOpsWorks() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getRancher() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getRightScale() != null) {
            //TODO: itay - check
        }

        if (blThirdPartiesIntegration.getRoute53() != null) {
            //TODO: itay - check
        }

        apiGroupBuilder.setThirdPartiesIntegration(apiThirdPartyBuilder.build());
    }
    //endregion
    //endregion


}
