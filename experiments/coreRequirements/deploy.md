# Deployment
### Hardware Requirements (TODO)
For a UMLP instance the server should have at least:
* (?) 5GB HDD,
* (?) 2 CPUs
* (?) 4 GB RAM

### Software Requirements
Any linux OS with (current!) [docker-compose installation](https://docs.docker.com/compose/install/).

Windows Server might be possible, but it is VERY hard to run linux-docker-container on windows server.

### Build Dockerimage
#### Backend
You can build and publish the dockerimage with one command. No docker installation is required.
````
cd <backendLocation>
gradle jib \ 
      -Djib.to.image=<imageNameOfBackend> \
      -Djib.to.auth.username=<RegistryUser>  \
      -Djib.to.auth.password=<RegistryPassword> 
````
For more options, see the [jib](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin) documentation.

#### Frontend
The frontend dockerimage comes with [certbot](https://certbot.eff.org/pages/about). 
Certbot automatically handles the SSL-certificate.

````
cd <frontendLocation>
gradle assemble
docker build .  -t <imageNameOfFrontend>
````

### Deploy on Server
* copy the `docker-compose.yml` file on the server
    * Modify the file
* In gitlab create login token with read access to package registry
* On the server:
    * `docker login -u <name> -p <token> registry.git.rwth-aachen.de`
    * `docker-compose pull`
    * `docker-compose -p <nameOfApplication> up --detach`
