# ========== Build stage ==========
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
# For Gradle projects:
RUN ./gradlew clean bootJar --no-daemon

# ========== Run stage ==========
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
