# script for running the backend as a container

# check if we have more than 1 argument
if ( $args.count -gt 1 )
{
  # print error statement, exit
  Write-Output "Whoa, too many arguments there, take it easy man"
  exit 1
}

if ( $args.count -eq 0 )
{
  Write-Output 'No arguments, defaulting to running in shell'
  $ARG = "-it"
}

if ( $args -eq "-h" )
{
  Write-Output '--------------------- run.sh help ---------------------'
  Write-Output 'Arguments: '
  Write-Output '  -d: [Detached Mode]'
  Write-Output '      runs the container detached from the terminal. '
  Write-Output '      Container will persist and run in the background, and '
  Write-Output '      the container ID will be printed to the terminal. '
  Write-Output ''
  Write-Output '  -it: [Interactive Mode]'
  Write-Output '      runs the container ATTACHED to the terminal '
  Write-Output '      any output to STDOUT will be written to the  '
  Write-Output '      terminal, and closing the terminal exits the '
  Write-Output '      the container. Functions identical to exec, replacing '
  Write-Output '      the terminal process with the docker process.'
  Write-Output ''
  Write-Output '---------------------- end ----------------------------'
  exit 1
}

 if ( ( $args -eq "-d") -or ( $args -eq "-it") -or ( $ARG -ne $null))
{
  $ARG = $args
} else
{
  Write-Output "$1 is not a valid argument, use -h for help"
  exit 1
}


# check if rt app already exists as a container
if ( "$(docker ps -aq -f status=exited -f name=rt-backend-app)" )
{
  # cleanup lifeless container and send it into the void
  Write-Output "container already exists, but is dead. removing..."
  docker rm --force -v rt-backend-app
} else
{
  Write-Output "container not found, creating new"
}

# Volume work deprecated, but will leafe the commands below in case it helps with future projects
## set up volume for mysql instance
#docker volume create mysql-local-volume
#
## run mysql container, expose on port 3306, and use the volume we just created
#docker run --name=mysql-rt -p 3307:3307 -v mysql-local-volume:/var/lib/mysql \
#-e MYSQL_ROOT_PASSWORD=testPass1234 \
#-d mysql/mysql-server:latest

# run the container on port 8080
Write-Output ''
Write-Output "running rt/backend:latest in $ARG mode..."
docker run "$ARG" --name=rt-backend-app rt/backend:latest
# docker run "$ARG" --name=rt-backend-app rt/backend:latest
