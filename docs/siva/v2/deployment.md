## Deployment diagram

![SiVa Deployment view](../../img/siva/uml_siva_deployment_diagram.png)

### Load balancer

Load balancer can distribute traffic between SiVa nodes when there is more then one instance running.
SiVa do not set any specific requirements for load balancer but in diagram the Nginx reverse proxy option is used.

### SiVa web application

SiVa web appliction is executable Spring Boot JAR file. This means all the dependencies and servlet containers are
packaged inside single JAR file. The JAR file can be placed anywhere in server and the JAR must be marked executable
if its not already.

There also should be separate user created to run executalbe JAR as Linux service.

Read more about running
[Spring Boot applications as Linux system service](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-service)

### SiVa X-Road validation service

SiVa X-Road validation service is also Spring Boot executable JAR application and also should be installed as Linux
service. X-Road validation service communicates with SiVa web application over HTTP and default port is 8081

X-Road separate installation is required to avoid BouncyCastle libraries version conflicts and class loader
issues.

### Security server

Into XRoad v6 security server the SiVa validation service will be registered to provide service to other organisations
using XRoad infrastructure. Setting up XRoad Security server is out of scope for SiVa documentaiton.

## SiVa System deployment

### System requirements

Following are minimum requirements to build and deploy SiVa validation
web service:

* Java 8 or above Oracle JVM is supported
* Git version control system version 1.8 or above is recommended
* Minimum 2 GB of RAM. Recommended at least 4 GB of RAM
* Minimum 1 processor core
* Open internet connection
* 1GB of free disk space
* Supported operating system is Ubuntu 14.04 LTS

### Building SiVa validation web service on Ubuntu 16.04

First we need to install Git and Java SDK 8 by issuing below commands:

```bash
sudo apt-get update
sudo apt-get install git -y
sudo apt-get install default-jdk -y
```

Next we need to clone the SiVa Github repository:

```bash
git clone https://github.com/open-eid/SiVa.git --branch develop
```

Final step is building the SiVa project using Maven Wrapper

```bash
cd SiVa
./mvnw install
```

!!! note
    The build can take up to **30 minutes** because there are lot of tests that will be run through and downloading of the
    required dependencies

To verify that SiVa project built successfully look for `BUILD SUCCESS` in build compilation output last lines.
The last lines of build output should look very similar to below image:

```text
[INFO] Reactor Summary:
[INFO]
[INFO] SiVa Digitally signed documents validation service . SUCCESS [ 25.258 s]
[INFO] validation-services-parent ......................... SUCCESS [  0.479 s]
[INFO] validation-commons ................................. SUCCESS [01:45 min]
[INFO] tsl-loader ......................................... SUCCESS [ 16.507 s]
[INFO] PDF Validation Service ............................. SUCCESS [ 42.263 s]
[INFO] BDOC Validation Service ............................ SUCCESS [ 58.864 s]
[INFO] DDOC Validation Service ............................ SUCCESS [  9.929 s]
[INFO] xroad-validation-service ........................... SUCCESS [  5.664 s]
[INFO] SIVa webapp and other core modules ................. SUCCESS [  0.315 s]
[INFO] SiVa validation service proxy ...................... SUCCESS [ 43.098 s]
[INFO] siva-webapp ........................................ SUCCESS [04:06 min]
[INFO] SiVa Sample Web application ........................ SUCCESS [04:31 min]
[INFO] SiVa Web Service integration tests ................. SUCCESS [03:41 min]
[INFO] siva-distribution .................................. SUCCESS [ 56.941 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 18:30 min
[INFO] Finished at: 2016-07-22T13:40:31+00:00
[INFO] Final Memory: 80M/250M
[INFO] ------------------------------------------------------------------------
```

### Setting up systemd service

Maven build generates executable JAR files. This means web container and all its dependencies are package inside
single JAR file. It makes a lot easier to deploy it into servers.

Easiest option to setup SiVa is as `systemd` service in Ubuntu servers.

For that we first need to create service file:
```bash
vim siva-webapp.service
```

Inside it we need to paste below text. You need and can change few things in service setup file.

* First you **must not** run service as `root`. So it's strongly recommended to change line `User=root`
* Second You can change Java JVM options by modifying the `JAVA_OPTS` inside the `siva-webapp.service` file.
* Also You can change the SiVa application configuration options by modifying `RUN_ARGS` section in file

```ini
[Unit]
Description=siva-webapp
After=syslog.target

[Service]
User=root
ExecStart=/var/apps/siva-webapp.jar
Environment=JAVA_OPTS=-Xmx320m RUN_ARGS=--server.port=80
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

Save and close the `siva-webapp.service` file.
Next we need to move `siva-webapp-2.0.2-SNAPSHOT.jar` into newly created `/var/apps` directory and rename to
JAR file to `siva-webapp.jar`. match

!!! note
    The copied JAR filename must match option `ExecStart` in  `siva-webaapp.servcie` file

```bash
sudo mkdir /var/apps
sudo cp siva-parent/siva-webapp/target/executable/siva-webapp-2.0.2-SNAPSHOT.jar /var/apps/siva-webapp.jar
```

Next we need to copy the `siva-webapp.service` file into `/lib/systemd/system` directory.
Then we are ready to start the `siva-webapp` service.

```bash
sudo cp siva-webapp.service /lib/systemd/system
sudo systemctl start siva-webapp
```

Final step of setting up the `siva-webapp` service is to verify that service started correctly by issuing below
command.

```bash
systemctl status siva-webapp
```

It should print out similar to below picture:

```
● siva-webapp.service - siva-webapp
   Loaded: loaded (/lib/systemd/system/siva-webapp.service; disabled; vendor preset: enabled)
   Active: active (running) since Thu 2016-07-21 08:48:14 EDT; 1 day 2h ago
 Main PID: 15965 (siva-webapp.jar)
    Tasks: 34
   Memory: 429.6M
      CPU: 2min 5.721s
   CGroup: /system.slice/siva-webapp.service
           ├─15965 /bin/bash /var/apps/stage/siva-webapp.jar
           └─15982 /usr/bin/java -Dsun.misc.URLClassPath.disableJarChecking=true -Xmx320m -jar /var/apps/stage/siva-webapp.jar

Jul 20 03:00:01 siva siva-webapp.jar[15965]:         at eu.europa.esig.dss.tsl.service.TSLParser.getTslModel(TSLParser.java:143)
Jul 20 03:00:01 siva siva-webapp.jar[15965]:         at eu.europa.esig.dss.tsl.service.TSLParser.call(TSLParser.java:129)
Jul 20 03:00:01 siva siva-webapp.jar[15965]:         ... 5 common frames omitted
Jul 20 03:00:01 siva siva-webapp.jar[15965]: 20.07.2016 03:00:01.450 INFO  [pool-3-thread-1] [e.e.e.dss.tsl.service.TSLRepository.sync
Jul 20 03:00:01 siva siva-webapp.jar[15965]: 20.07.2016 03:00:01.450 INFO  [pool-3-thread-1] [e.e.e.dss.tsl.service.TSLRepository.sync
```

### Installing HTTPIE

`httpie` is more user friendly version of `curl` and we will use to verify that SiVa was installed
and started correctly on our server.

### Ubuntu 16.04

On Ubuntu You can install it using `apt` package manager:

````bash
sudo apt-get install -y httpie
````

### Mac OS X

On Mac it's strongly recommended to install it using package manager like Homebrew by issuing
belwo command:

```bash
brew install httpie
```

#### Windows

On Windows there is no prebuilt package that can be installed but `httpie` installation instruction in
[Scott Hanselmans blog post](http://www.hanselman.com/blog/InstallingHTTPIEHTTPForHumansOnWindowsGreatForASPNETWebAPIAndRESTfulJSONServices.aspx)

### Cross platform

Alternative option if You have Python and its package manager `pip` installed. Then You can issue
below command:

```bash
pip install httpie
```

### Verify digitally signed file

After You have successfully installed HTTPIE. Then we need to download sample JSON request file with below
command

```bash
http --download https://raw.githubusercontent.com/open-eid/SiVa/develop/build-helpers/bdoc_pass.json
```

After successful download issue below command in same directory where You downloaded the file using
the above command.

```bash
http POST http://10.211.55.9:8080/validate < bdoc_pass.json
```

Output of this command should look like below screenshot. Look for `signatureCount` and
`validSignatureCount` they **must** be equal.


![HTTPIE output validation](../../img/siva/siva-output.png)

### SiVa configuration overrides

All override configuration properties

```properties
# BDOC validation service override properties
siva.bdoc.digidoc4JConfigurationFile=/path/to/digidoc4j.yml
siva.bdoc.signaturePolicy.defaultPolicy=policy_name
siva.bdoc.signaturePolicy.policies.pol_v1=/path/to/policy1.xml
siva.bdoc.signaturePolicy.policies.pol_v2=/path/to/policy2.xml

# DDOC validation service override properties
siva.ddoc.jdigidocConfigurationFile=/path/to/jdigidoc.cfg
siva.ddoc.signaturePolicy.defaultPolicy=policy_name
siva.ddoc.signaturePolicy.policies.pol_v1=/path/to/policy1.xml
siva.ddoc.signaturePolicy.policies.pol_v2=/path/to/policy2.xml

# PadES validation service override properties
siva.pdf.signaturePolicy.defaultPolicy=policy_name
siva.pdf.signaturePolicy.policies.pol_v1=/path/to/policy1.xml
siva.pdf.signaturePolicy.policies.pol_v2=/path/to/policy2.xml

# X-road validation service settings
siva.xroad.validation.service.configurationDirectoryPath=''
siva.xroad.signaturePolicy.defaultPolicy=policy_name
siva.xroad.signaturePolicy.policies.pol_v1=/path/to/policy1.xml
siva.xroad.signaturePolicy.policies.pol_v2=/path/to/policy2.xml

# TSL trusted certificaes keystore settings
siva.keystore.type=JKS
siva.keystore.filename=/path/to/siva-keystore.jks
siva.keystore.password=siva-keystore-password

# SiVa Proxy Settings
siva.proxy.xroadUrl=http://localhost:8081

siva.statistics.google-analytics.url=http://www.google-analytics.com/batch
siva.statistics.google-analytics.trackingId=UA-83206619-1
siva.statistics.google-analytics.dataSourceName=SiVa

siva.tsl.loader.loadFromCache=false
siva.tsl.loader.url=https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml
siva.tsl.loader.code=EU
siva.tsl.loader.schedulerCron=0 0 3 * * ?
```