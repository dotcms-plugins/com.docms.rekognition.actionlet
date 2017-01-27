# AWS Rekognition Workflow Action for dotCMS

This OSGi plugin provides support for Amazon's Rekognition AI engine to automatically tag images managed by dotCMS.  This actionlet can be added to any workflow step and if the content object being passed to the workflow has an binary image field and a tag field then it will automatically tag the image for the user.

In order to use this workflow action, you will need access to amazon's AWS.  The AWS key and secret can be changed in the aws-rekog.properties file found under src/main/resources

```
#REKOGNITION
aws.key=12341234
aws.secret=12341234
max.labels=20
min.confidence=77
```


