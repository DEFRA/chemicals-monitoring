@Library('jenkins-shared-library')_
def helper = new helpers.PipelineHelper(this);

node (label: 'autoSlaveLive') {
  def URL = "reach-monitoring"
  def RESOURCE = "SNDCHMINFRGP001-${URL}-${helper.getEnvSuffix()}"
  def APP = "${URL}-${helper.getEnvSuffix()}"
  def AI = "SNDCHMINFRGP001-${helper.getEnvSuffix()}"
  def EVENT_HUB_CONNECTION_STRING = "${sh(script: "set -e; az eventhubs namespace authorization-rule keys list --subscription AZR-SND --resource-group SNDCHMINFRGP001 --namespace-name SNDCHMINFENS001 --name RootManageSharedAccessKey | jq '.primaryConnectionString' -r", returnStdout: true, label: "Fetch eventhub key").toString().trim()};EntityPath=sndchmeventhubtest"
  def REACH_MONITORING_ENV = "dev"
  def MONITORING_EVENT_VERSION = "1.1"

  def environmentVariables = [
      "APP_NAME=${APP}",
      "SERVICE_NAME=REACH Monitoring",
      "URL_PATH=${URL}",
      "ACR_REPO=reach-monitoring/reach-monitoring",
      "CONNECTION_STRING=HTTP_PROTECTIVE_MONITORING_SERVICE_PORT=8080 WEBSITES_PORT=8080 JWT_SECRET_KEY='MySecretKey' EVENT_HUB_CONNECTION_STRING='${EVENT_HUB_CONNECTION_STRING}' REACH_MONITORING_ENV='${REACH_MONITORING_ENV}' MONITORING_EVENT_VERSION='${MONITORING_EVENT_VERSION}'",
      "AI_NAME=${AI}",
      "RESOURCE_GROUP=${RESOURCE}",
      "BACKEND_PLAN=SNDCHMINFRGP001-${URL}-${helper.getEnvSuffix()}-service-plan",
      "SET_APP_LOGGING=true",
      "RUN_SONAR=true",
      "PROJECT_REPO_URL=https://giteux.azure.defra.cloud/chemicals/reach-monitoring.git"
    ]

  withEnv(environmentVariables) {
    def CREATE_DB = []
    reachPipeline(CREATE_DB)
  }
}
