# RT-Contracts
Back-end architecture for the Radio Telescope Senior Software Design Project for the 2018-2019 academic year

1. Install MySQL
* https://dev.mysql.com/downloads/mysql/
* username and password for the localhost db should be "root"
* start mysql - "mysql.server start"
* for linux, the command "sudo systemctl start mysql" or "sudo systemctl start mysqld" depedning on the linux distro
* use "stop" and "restart" when needed

2. Install Gradle
* https://gradle.org/install

3. Initialize Database
* Open Database Tab in IntelliJ
* Add New MySQL Datasource & Test Connection (using credentials used for MySQL installation)
Note: specify the database url as the one found in the local application properties file
* If the connection works, you're good to go!

4. Setup Gradle Wrapper 
* Open IntelliJ Terminal and type "gradle wrapper" command

