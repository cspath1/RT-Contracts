#!/usr/bin/env bash
# script for running the backend as a container

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
    if [ "$1" = "-h" ];
      then
        echo '--------------------- run.sh help ---------------------'
        echo 'Arguments: '
        echo '  -d: [Detached Mode]'
        echo '      runs the container detached from the terminal. '
        echo '      Container will persist and run in the background, and '
        echo '      the container ID will be printed to the terminal. '
        echo ''
        echo '  -it: [Interactive Mode]'
        echo '      runs the container ATTACHED to the terminal '
        echo '      any output to STDOUT will be written to the  '
        echo '      terminal, and closing the terminal exits the '
        echo '      the container. Functions identical to exec, replacing '
        echo '      the terminal process with the docker process.'
        echo ''
        echo '---------------------- end ----------------------------'
        exit 1
    fi
    if [[ ("$1" = "-d")  || ("$1" = "-it") ]];
      then
        ARG=$1
      else
        echo "$1 is not a valid argument, use -h for help"
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
