# build the container, name it and tag it as the latest build
# this command will fail if you have not first built the .jar file
# that is the "executable" of the java world. Run [gradle build] to make this file
docker build -t rt/backend:latest .