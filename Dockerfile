FROM maven:3.6.3-jdk-11 AS BACKEND_BUILD
MAINTAINER Iurii Berezin
ARG profile
ENV profile ${profile}
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn -P$profile package -DskipTests

FROM node:14.2.0-alpine3.10 AS FRONTEND_BUILD
WORKDIR /build
COPY package.json /build/
RUN npm install
RUN npm install webpack -g
RUN npm install webpack-cli -g
RUN npm install -D
RUN mkdir -p /build/src/main/resources/static/built
COPY webpack.config.js /build/
COPY src/main/js /build/src/main/js
RUN webpack

FROM openjdk:11-jre-slim
WORKDIR /app
COPY startApp.sh /app/
COPY --from=BACKEND_BUILD /build/target/hat-online-0.0.1-SNAPSHOT.jar /app/
COPY --from=FRONTEND_BUILD /build/node_modules /app/node_modules
COPY --from=FRONTEND_BUILD /build/src/main/resources/static/built /app/src/main/resources/static/built
ARG profile
ENV profile ${profile}
#ENTRYPOINT ["java","-Dspring.profiles.active=${profile}", "-jar", "hat-online-0.0.1-SNAPSHOT.jar"]
CMD ["sh", "-c", "./startApp.sh ${profile}"]