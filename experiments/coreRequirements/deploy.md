<!-- (c) https://github.com/MontiCore/monticore -->
# Deployment
### Hardware Requirements (TODO)
For a UMLP instance the server should have at least:
* (?) 5GB HDD,
* (?) 2 CPUs
* (?) 4 GB RAM

### Software Requirements
* Any linux OS with (current!) [docker-compose installation](https://docs.docker.com/compose/install/).
  * Windows Server might be possible, but it is VERY hard to run linux-docker-container on windows server.
* Open Port 80 and 443 on TCP
* Domain
  * Let's Encrypt must be allowed to issue certificates for the domain. Might require DNS configuration. 
### Build Dockerimage
#### Backend
The usage of [jib](https://github.com/GoogleContainerTools/jib/) as gradle-plugin is recommended. 
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
The frontend dockerimage is using [Caddy](https://caddyserver.com/) to serve static files, proxy to backend and handle SSL certificates

Following files are required in the frontend Directory:
* `Dockerfile`
  * the ``dist/<frontendName>`` directory-name might be modified 
* `Caddyfile`

````
cd <frontendLocation>
gradle assemble
docker build .  -t <imageNameOfFrontend>
````

### Deploy on Server
* copy the `docker-compose.yml` file on the server
  * e.g. ``scp ./docker-compose.yml user@domain.de:~/`` 
  * Modify the file according to comments
* In gitlab create login token with read access to package registry
* On the server:
    * `docker login -u user -p <token> registry.git.rwth-aachen.de`
    * `docker compose pull`
    * `docker compose up --detach`

#### Deploy without Certbot / SSL
Look at `Dockerfile-no-certbot` in the frontend.

The frontend configuration in docker-compose should be extended with a volume: 
````
frontend:
  [...]
  volumes:
    - './<certLocation>:/usr/local/apache2/conf/ssl/:ro'
````

the ``<certLocation>/httpd-ssl.conf`` should contain a virtual host, which listens on port 443. e.g. 
````
# generated 2021-09-22, Mozilla Guideline v5.6, Apache 2.4.49, OpenSSL 1.1.11, intermediate configuration, no HSTS, no OCSP
# https://ssl-config.mozilla.org/#server=apache&version=2.4.49&config=intermediate&openssl=1.1.11&hsts=false&ocsp=false&guideline=5.6

# this configuration requires mod_ssl
<VirtualHost *:443>
    SSLEngine on

    # curl https://ssl-config.mozilla.org/ffdhe2048.txt >> /path/to/signed_cert_and_intermediate_certs_and_dhparams
    SSLCertificateFile      conf/ssl/public.pem
    SSLCertificateKeyFile   conf/ssl/private.pem

    # enable HTTP/2, if available
    Protocols h2 http/1.1

    ## UMLP Specific (e.g. redirect to backend)
    Include conf/httpd-umlp.conf

</VirtualHost>

# intermediate configuration
SSLProtocol             all -SSLv3 -TLSv1 -TLSv1.1
SSLCipherSuite          ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384
SSLHonorCipherOrder     off
SSLSessionTickets       off
````
