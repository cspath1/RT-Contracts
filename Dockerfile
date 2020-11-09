# YCP RT Dockerfile
# used to create a container which can run a .jar
# multi stage dockerfile. first stage pulls gralde 4.9 and creates the .jar
# then, the second stage copies the .jar made over and creates the end image
# ref: https://docs.docker.com/engine/reference/builder/

# JDK 8 base image slim version
# ref: https://docs.docker.com/engine/reference/builder/#from
FROM openjdk:8-jre-slim

# set dir in docker file (like cd'ing into it)
# ref: https://docs.docker.com/engine/reference/builder/#workdir
WORKDIR /usr/

# COPY /home/gradle/src/build/libs/*.jar usr/app.jar
COPY ./build/libs/radio-telescope-4.2.1.jar /usr/app.jar

# port we will use to talk to our container over
# defaults to TCP, but doesn't hurt to be explicit
# ref: https://docs.docker.com/engine/reference/builder/#expose

EXPOSE 8080/tcp

# think of this as a command you would run in a terminal to start
# the app. With the base image we are pulling from (JDK 8), we are creating
# an interfaceless ligthweight virtual machine. ENTRYPOINT specifies the first
# command to be run when the container is ran.

ENTRYPOINT ["java", "-jar", "app.jar"]
