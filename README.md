# RT-Contracts

## Web-Application Back-End Server for YCAS Radio Telescope

### GitHub Repository
Website URL: https://github.com/cspath1/RT-Contracts

Clone command (https): ```git clone https://github.com/cspath1/RT-Contracts```

Clone command (git): ```git@github.com:cspath1/RT-Contracts```

### Repository Owner
Name: Cody Spath

GitHub Account: cspath1

Email Address: cspath1@ycp.edu

### Amazon Web Services Account Owner
Name: Cody Spath

Email Address: cspath1@ycp.edu

### Installation

The following technologies are used in the development of the back-end application:

* MySQL Database (SQL Database/Dialect)
* Gradle (Build Management/Task Execution)
* Spring (Application Framework)
* IntelliJ IDEA Ultimate Edition (IDE)
* Kotlin (Programming Language)
* Liquibase (Database Migrations)
* Travis CI (Continuous Integration Service)
* GitHub (Version Control)

### GitHub Repository Setup

You can clone the repository using either the https or the git commands supplied above, but in order to contribute
to the repository, you must be added as a contributor. To achieve this, contact the repository owner (mentioned 
above), and they will handle this.

### IntelliJ IDEA Setup

IntelliJ IDEA Ultimate Edition is free for anyone with a student license, which thankfully, applies to anyone
with an ".edu" email address. All you need to do is create an account on [their website](https://account.jetbrains.com),
and from there, you can access all software that applies to you via the student license. It is important to note that the student license can only be used for student projects, which this project falls under.

#### Windows Setup

The process on windows is rather straightforward, and is primarily handled through an installer/setup wizard. 

#### Linux Setup

The steps needed to install via Linux are as follows:

1. Download the latest version IntelliJ IDEA Ultimate Edition
2. Extract the tar file using (tar -xvf <filename>)
3. Move the newly extracted folder to the /opt folder (sudo mv <folder-name> /opt/)
4. Go to the folder location in the opt folder (cd /opt/<folder-name>)
5. Go inside of the IntelliJ IDEA bin folder (cd bin)
6. Run the idea.sh script. This will run the initial setup for IntelliJ IDEA (./idea.sh)
7. You will then be prompted by IntelliJ to enter some user-specific preferences for IntelliJ IDEA.
8. After choosing your preferences, verify your installation using your JetBrains account 

#### Mac Setup
**CURRENTLY UNKNOWN**

### Gradle Setup

#### Windows Setup
For Windows users, you should be able to follow the steps [here](https://gradle.org/install) to install Gradle.

#### Linux Setup
If you are using Linux, it is highly recommended to [install SDKMAN](https://sdkman.io/), which allows you
to then easily install Gradle using SDKMAN. The list of steps to install Gradle on linux are:

1. Install SDKMAN (curl -s “https://get.sdkman.io” | bash)
2. Initialize SDKMAN (source "$HOME/.sdkman/bin/sdkman-init.sh")
3. Verify the installation was a success (sdk version)
4. Use SDKMAN to install Gradle (sdk install gradle) 
    1. Note: This will install the most recent version
    2. To install a specific version, supply the version (sdk install gradle 4.7)
5. Verify the installation was a success (gradle -v)

#### Mac Setup
**CURRENTLY UNKNOWN**

### Install MySQL

#### Windows Setup
For Windows users, you should be able to follow the steps/setup wizard [here](https://dev.mysql.com/downloads/mysql/)

#### Linux Setup
If you are using linux, do the following:

1. Update package lists (sudo apt-get update)
2. Install MySQL Server (sudo apt-get install mysql-server)
3. Start MySQL Server (sudo systemctl start mysql) or (sudo systemctl start mysqld) depending on your linux distro
4. Secure the installation (sudo mysql_secure_installation). This will have you enter the root username and password
Verify your credentials (mysql -u root -p). This will prompt you to enter the password you entered in step 4.

#### Mac Setup
**CURRENTLY UNKNOWN**

### Initialize Database in IntelliJ

IntelliJ has a built-in database tab that allows you to manage the contents of your database from within the IDE.
In order to add your localhost database to IntelliJ, do the following:

1. Open Database Tab in IntelliJ
2. Add New MySQL Datasource & Test Connection (using credentials used for MySQL installation)
Note: specify the database url as the one found in the local application properties file
3. If the connection works, you're good to go!

### Install Gradle Wrapper

In order to install the gradle wrapper (needed to boot up the application locally), issue the following
command in the terminal inside of IntelliJ:
```gradle wrapper```

### Properties Files

The application depends on certain application properties files that unfortunately cannot be added to GitHub.
These files must be obtained from the repository owner.

### Javadocs

The Radio Telescope Contracts/Back-End application uses javadocs to publish documentation. This process can be
done manually via the following process: 

1. Publishing javadocs in kotlin is done via [dokka](https://github.com/Kotlin/dokka)
2. In the build.gradle file, run the dokka task, or in the terminal run 'gradle dokka' to generate the documentation folder
3. Take the contents of the 'docs' folder that was created and put it in the gh-pages branch
4. **NOTE**: overwrite any existing files in the gh-pages branch with the contents of the newly generate docs folder
5. Still on the gh-pages branch, add, commit, and push the changes and GitHub will handle the rest

Alternatively, if you cloned the repository via git/ssh, you can simply run the following command,
which will automate the above process for you (recommended):

1. Run the ```./publish-api-docs.sh``` script

The docs can be viewed [here](https://cspath1.github.io/RT-Contracts). As a general rule of thumb, the docs
should be published whenever code is merged into master and deployed to the production server.

### Amazon Web Service Management Console

The management console for the web application (along with the services used for this project) can be 
accessed [here](https://317377631261.signin.aws.amazon.com/console).

In order to access the management console, you need an IAM User account. If you do not have one, contact the 
console root user (mentioned above) and they will create an IAM user for you and send you the credentials.
