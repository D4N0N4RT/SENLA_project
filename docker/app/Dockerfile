FROM adoptopenjdk:11-jre-hotspot
# указываем, где в нашем приложении лежит джарник
# ARG JAR_FILE=finale-0.1.jar
# добавляем джарник в образ под именем rebounder-chain-backend.jar
ADD finale-0.1.jar app.jar
# ENTRYPOINT ["java","-jar","./app.jar"]
# CMD java -jar ./app.jar