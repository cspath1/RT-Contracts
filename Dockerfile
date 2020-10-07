# YCP RT Dockerfile
# used to create a container which can run a .jar
# ref: https://docs.docker.com/engine/reference/builder/

# JDK 8 base image
# ref: https://docs.docker.com/engine/reference/builder/#from
FROM java:8-jdk-apline

# copy over .jar created using gradle build
# NOTE: you may need to update your gradle version to 4.9 to
#       build the app
# ref: https://docs.docker.com/engine/reference/builder/#copy
COPY ./build/libs/radio-telescope-4.2.1.jar /usr/app

# set dir in docker file (like cd'ing into it)
# ref: https://docs.docker.com/engine/reference/builder/#workdir
WORKDIR /usr/app

# port we will use to talk to our container over
# defaults to TCP, but doesn't hurt to be explicit
# ref: https://docs.docker.com/engine/reference/builder/#expose
EXPOSE 4040/tcp

# think of this as a command you would run in a terminal to start
# the app. With the base image we are pulling from (JDK 8), we are creating
# an interfaceless ligthweight virtual machine. ENTRYPOINT specifies the first
# command to be run when the container is ran.
ENTRYPOINT ["java", "-jar", "radio-telescope-4.2.1.jar"]