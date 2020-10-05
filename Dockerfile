FROM java:8-jdk-apline
COPY ./build/libs/radio-telescope-4.2.1.jar /usr/app
WORKDIR /usr/app

