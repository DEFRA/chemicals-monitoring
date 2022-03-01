## REACH Monitoring

### Build

To build the Monitoring service locally 

1. build the application jar via Maven command
   
   `mvn clean install -U`

2. check env variables in `docker/docker-compose.yml` are set appropriate for your local dev environment
   
3. Build the docker image and run the container

```
cd docker
# check pom.xml for setting the argument BUILD_VERSION
docker-compose build --build-arg BUILD_VERSION=3.3.0-SNAPSHOT
# check the docker-composse file and ensure the env variable `EVENT_HUB_CONNECTION_STRING` is set correctly
docker-compose up --no-build
```
