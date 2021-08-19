LAST_UPDATE_TIME_FILE=last_updated.txt
aws cloudformation deploy --template-file ./templates/code.yaml --stack-name code-stack

BUCKET_NAME=$(aws ssm get-parameter --name "/buckets/code/name" | jq -r .Parameter.Value)

if [ ! -f $LAST_UPDATE_TIME_FILE ]; then
  LAST_UPDATED_TIME=0
else
  LAST_UPDATED_TIME=$(cat $LAST_UPDATE_TIME_FILE)
fi

LATEST_MODIFIED=$(find src -type f -exec stat -f %m {} \; | sort -rn | head -n 1)
POM_TIME=$(date -r pom.xml +%s)

# TODO: Figure out why this isn't working
#if [ "${LATEST_MODIFIED}" -gt "${LAST_UPDATED_TIME}" ] | [ "${POM_TIME}" -gt "${LAST_UPDATED_TIME}" ]; then
#  mvn clean install
#fi

mvn clean install

aws s3 sync --exclude "*" --include "target/cli-api-*.jar" --include "templates/*" . "s3://${BUCKET_NAME}/cli-api/"

aws cloudformation deploy --template-file ./templates/parent.yaml --stack-name parent-stack --capabilities CAPABILITY_IAM

LAMBDA_NAME=$(aws ssm get-parameter --name "/apis/cli/lambda/name" | jq -r .Parameter.Value)
aws lambda update-function-code --function-name "${LAMBDA_NAME}" --s3-bucket "${BUCKET_NAME}" --s3-key "cli-api/target/cli-api-1.0-SNAPSHOT.jar" > /dev/null

LAST_UPDATED_TIME=$(date +%s)
echo "${LAST_UPDATED_TIME}" >$LAST_UPDATE_TIME_FILE
