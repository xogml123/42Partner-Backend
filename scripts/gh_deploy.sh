#!/bin/bash
PROJECT_PATH="/home/ec2-user/app/42Partner-Backend"
MODULE_NAME="module-api"
CONTEXT="dev"
JAR_PATH="$PROJECT_PATH/$MODULE_NAME/build/libs/*.jar"
DEPLOY_PATH=$PROJECT_PATH/$MODULE_NAME
DEPLOY_LOG_PATH="$PROJECT_PATH/$MODULE_NAME/deploy.log"
DEPLOY_ERR_LOG_PATH="$PROJECT_PATH/$MODULE_NAME/deploy_err.log"
APPLICATION_LOG_PATH="$PROJECT_PATH/$MODULE_NAME/application.log"
LOGBACK_CONFIG_PATH="$DEPLOY_PATH/src/main/resources/logback-spring.xml"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)

echo "===== 배포 시작 : $(date +%c) =====" >> $DEPLOY_LOG_PATH

echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 동작중인 어플리케이션 pid 체크" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 동작중인 어플리케이션 존재 X" >> $DEPLOY_LOG_PATH
else
  echo "> 현재 동작중인 어플리케이션 존재 O" >> $DEPLOY_LOG_PATH
  echo "> 현재 동작중인 어플리케이션 강제 종료 진행" >> $DEPLOY_LOG_PATH
  echo "> kill -9 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  kill -9 $CURRENT_PID
fi

DEPLOY_JAR="$DEPLOY_PATH/build/libs/$JAR_NAME"
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH
sudo nohup java -jar -Dlogback.configurationFile=$LOGBACK_CONFIG_PATH -Dspring.profiles.active=prod $DEPLOY_JAR  &

sleep 3
echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH