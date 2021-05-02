FROM maven:3.8.1-jdk-11 as BUILD
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR/parent
RUN mkdir -p $PROJECT_DIR/child

ADD ./pom.xml $PROJECT_DIR/parent
WORKDIR $PROJECT_DIR/parent
RUN mvn install -N

ADD ./bookeeper/pom.xml $PROJECT_DIR/child
WORKDIR $PROJECT_DIR/child
RUN mvn dependency:resolve

ADD ./bookeeper/src/ $PROJECT_DIR/child/src
RUN mvn install -DskipTests

FROM openjdk:11-jre-slim
ENV PROJECT_DIR=/opt/project
WORKDIR $PROJECT_DIR
COPY --from=build $PROJECT_DIR/child/target/bookeeper* $PROJECT_DIR
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/project/bookeeper-1.0.jar"]
