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

5. Publishing Javadocs
* Publishing javadocs in kotlin is done via [dokka](https://github.com/Kotlin/dokka)
* In the build.gradle file, run the dokka task, or in the terminal run 'gradle dokka' to generate the documentation folder
* Take the contents of the 'docs' folder that was created and put it in the gh-pages branch
* **NOTE**: overwrite any existing files in the gh-pages branch with the contents of the newly generate docs folder
* Still on the gh-pages branch, add, commit, and push the changes and GitHub will handle the rest
* **ANOTHER NOTE** there is a publish-api-docs.sh file that will handle all of this work for you. Simply run "gradle dokka" and then "./publish-api.docs.sh" and you're good to go (as long as you have the project clone via ssh. Cloning via HTTPS **WILL NOT** work).
* Go [here](https://cspath1.github.io/RT-Contracts) to view the docs