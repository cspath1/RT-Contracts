#!/usr/bin/env bash

# if container already exists (running or not), needs to be removed
# if no container exists, this will fail but will not interrupt the script
#echo 'attempting to remove previous container...'
#docker rm --force rt-backend-app
#
#
## running container on port 8080, naming it rt-backend-app, using latest image
#docker run -it -p 8080:8080 --name rt-backend-app rt/backend:latest

# check if we have more than 1 argument
if [ $# -gt 1 ]; then
  # print error statement, exit
  echo 'Whoa, too many arguments there, take it easy man'
  exit 1
fi

if [ ! "$1" ];
then
  echo 'No arguments, defaulting to running in shell'
  ARG='-it'
  else
    if [[ ("$1" = "-d")  || ("$1" = "-it") ]];
      then
        ARG=$1
      else
        echo "$1 is not a valid argument, needs to be -d or -it for detached mode or exec mode"
        exit 1
    fi
fi

#
if [ "$(docker ps -aq -f status=exited -f name=rt-backend-app)" ];
  then
    # cleanup lifeless container and send it into the void
    echo 'container already exists, but is dead. removing...'
    docker rm --force -v rt-backend-app
  else
    echo 'container not found, creating new'
fi

# run the container on port 8080
echo ''
echo "running rt/backend:latest in $ARG mode..."
docker run "$ARG" -p 8080:8080 --name rt-backend-app rt/backend:latest
