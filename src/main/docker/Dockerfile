FROM amazoncorretto:21
#COPY --from=maven_build /target/*.jar application.jar
COPY *.jar application.jar
ENTRYPOINT ["java", "-jar",  "application.jar"]
