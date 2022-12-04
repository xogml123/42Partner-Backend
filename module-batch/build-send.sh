./gradlew build -x test -Pprofile=dev
echo 'Taeh202020!!' | sudo -S -k scp -i "/Users/takim/.ssh/42partner-demo.pem" ~/42Partner-Backend/module-batch/build/libs/module-batch-0.0.1-SNAPSHOT.jar ec2-user@ec2-15-165-146-60.ap-northeast-2.compute.amazonaws.com:/home/ec2-user/app/42Partner-Backend/module-batch/build/libs

echo 'Taeh202020!!' | sudo -S -k scp -r -i "/Users/takim/.ssh/42partner-demo.pem" ~/42Partner-Backend/module-batch/src/main/resources-dev/application.yml ec2-user@ec2-15-165-146-60.ap-northeast-2.compute.amazonaws.com:/home/ec2-user/app/42Partner-Backend/module-batch/src/main/resources/resources-dev/application.yml
