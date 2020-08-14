# Merged into the core as of 5.2.7







# AWS Rekognition Workflow Action for dotCMS

This OSGi plugin provides support for Amazon's Rekognition AI engine to automatically tag images managed by dotCMS.  This actionlet can be added to any workflow step and if the content object being passed to the workflow has an binary image field and a tag field then it will automatically tag the image for the user.

In order to use this workflow action, you will need access to amazon's AWS.  The AWS key and secret can be changed in the aws-rekog.properties file found under src/main/resources

```
#REKOGNITION
aws.key=12341234
aws.secret=12341234

#defaults
max.labels=20
min.confidence=77
```


## Examples
![screen shot 2017-01-27 at 11 24 57 am](https://cloud.githubusercontent.com/assets/934364/22378718/8e1aa132-e484-11e6-8d45-0d896ac32d16.png)
---
![screen shot 2017-01-27 at 11 04 57 am](https://cloud.githubusercontent.com/assets/934364/22378725/90ca0e0e-e484-11e6-9207-27e00b5abea8.png)
---
![screen shot 2017-01-27 at 11 28 06 am](https://cloud.githubusercontent.com/assets/934364/22378730/92886f9c-e484-11e6-9f6c-0c0b21344e92.png)
