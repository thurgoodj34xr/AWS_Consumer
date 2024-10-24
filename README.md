# Dependencies 
- Gradle 8.10.2
- JVM 17

# Unit Tests
Run `gradle test`

# Build 
Run `gradle build`  
.jar file will be found in build/libs/consumer.jar  
Upload and deploy using means previous described in homeworks.  

## Defaults
Because this is just my stuff the defaults are set to my buckets, by using CLI it will override the bucket names, however the lack of bucket names will revert to my buckets and attempt to post to S3.  
Also it does not distinguish between create, update and delete requests, that was not required of me yet, it will be in the next instance.