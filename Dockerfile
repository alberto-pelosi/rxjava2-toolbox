FROM openjdk:14-alpine
COPY target/rxjava2-*.jar rxjava2.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "rxjava2.jar"]