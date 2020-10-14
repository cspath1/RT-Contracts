# YCP RT Dockerfile
# used to create a container which can run a .jar
# multi stage dockerfile. first stage pulls gralde 4.9 and creates the .jar
# then, the second stage copies the .jar made over and creates the end image
# ref: https://docs.docker.com/engine/reference/builder/

# grag gradle 4.9 so we can build our jar
FROM gradle:4.9 AS builder
WORKDIR /home/gradle/src
# chown gradle so it is runnable
COPY --chown=gradle:gradle . /home/gradle/src

# build the jar
RUN gradle assemble

# JDK 8 base image slim version
# ref: https://docs.docker.com/engine/reference/builder/#from
FROM openjdk:8-jre-slim

# copy over .jar created using gradle build
# NOTE: you may need to update your gradle version to 4.9 to
#       build the app
# ref: https://docs.docker.com/engine/reference/builder/#copy

COPY --from=builder /home/gradle/src/build/libs/*.jar usr/app.jar
# COPY ./build/libs/radio-telescope-4.2.1.jar /usr/app.jar

# set dir in docker file (like cd'ing into it)
# ref: https://docs.docker.com/engine/reference/builder/#workdir
WORKDIR /usr/

# port we will use to talk to our container over
# defaults to TCP, but doesn't hurt to be explicit
# ref: https://docs.docker.com/engine/reference/builder/#expose

EXPOSE 8080/tcp

# think of this as a command you would run in a terminal to start
# the app. With the base image we are pulling from (JDK 8), we are creating
# an interfaceless ligthweight virtual machine. ENTRYPOINT specifies the first
# command to be run when the container is ran.

ENTRYPOINT ["java", "-jar", "app.jar"]
