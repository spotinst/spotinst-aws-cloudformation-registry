# Elastigroup provider 

Usage example.
Add the following parameters to your CFN schema.

Credentials - Your spotinst account id and access token.

Group - Elastigroup json schema. For more information see our API documentation: [Elastigroup API](https://api.spotinst.com/spotinst-api/elastigroup/amazon-web-services/create/) 

 

```json
{
    "AWSTemplateFormatVersion": "2010-09-09",
    "Description": "Spotinst elastigroup provider template example ",
    "Resources":{
        "Elastigroup":{
            "Type": "Spotinst::Elastigroup::Group",
            "Properties":{
                "credentials": {
                    "accessToken": "SPOTINST_ACCESS_TOKEN",
                    "accountId": "SPOTINST_ACCOUNT_ID"
                  },
                  "group": {
                    "name": "GroupuWithEcs",
                    "region": "us-west-2",
                    "capacity": {
                      "minimum": 1,
                      "maximum": 3,
                      "target": 1,
                      "unit": "instance"
                    },
                    "strategy": {
                      "risk": 100,
                      "availabilityVsCost": "balanced",
                      "drainingTimeout": 60,
                      "lifetimePeriod": "days",
                      "fallbackToOd": true,
                      "revertToSpot": {
                        "performAt": "always"
                      }
                    },
                    "compute": {
                      "instanceTypes": {
                        "ondemand": "m4.xlarge",
                        "spot": [
                          "r4.2xlarge",
                          "c5.xlarge",
                          "c5.4xlarge",
                          "m5.xlarge",
                          "m5.2xlarge",
                          "m4.xlarge",
                          "m4.2xlarge",
                          "r4.4xlarge",
                          "r5.4xlarge",
                          "r4.xlarge",
                          "r5.xlarge",
                          "c4.4xlarge",
                          "c4.xlarge",
                          "r5.2xlarge",
                          "c4.2xlarge",
                          "m4.4xlarge",
                          "c5.2xlarge",
                          "m5.4xlarge"
                        ]
                      },
                      "availabilityZones": [
                        {
                          "name": "us-west-2a",
                          "subnetIds": [
                            "subnet-79da021e"
                          ]
                        },
                        {
                          "name": "us-west-2b",
                          "subnetIds": [
                            "subnet-0cb5a07e1df98b3fd"
                          ]
                        }
                      ],
                      "product": "Linux/UNIX",
                      "launchSpecification": {
                        "healthCheckType": "ECS_CLUSTER_INSTANCE",
                        "healthCheckGracePeriod": 300,

                        "securityGroupIds": [
                          "sg-123"
                        ],
                        "monitoring": false,
                        "ebsOptimized": false,
                        "imageId": "ami-123",
                        "iamRole": {
                          "arn": "some-arn"
                        },
                        "userData": "USER_DATA",
                        "blockDeviceMappings": [
                            {
                              "deviceName": "/dev/sda1",
                              "ebs": {
                                "deleteOnTermination": true,
                                "volumeSize": 48,
                                "volumeType": "standard"
                              }
                            }
                          ]
                      }
                    },
                    "thirdPartiesIntegration": {
                      "ecs": {
                        "clusterName": "CLUSTER_NAME",
                        "autoScale": {
                          "isEnabled": true,
                          "cooldown": 300,
                          "isAutoConfig": true,
                          "shouldScaleDownNonServiceTasks": false
                        }
                      }
                    }
                  }
            }
        }
    }
}
```




