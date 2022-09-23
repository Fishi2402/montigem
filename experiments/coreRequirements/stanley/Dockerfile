FROM httpd:2.4
MAINTAINER "Software Engineering, RWTH Aachen University"

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD wget localhost:80 -q -O - > /dev/null 2>&1

#RUN apk add --no-cache certbot certbot-apache
RUN apt-get update && apt-get install -y \
  certbot python3-certbot-apache  \
  && rm -rf /var/lib/apt/lists/*


## Apache Configuration (e.g. Proxy to backend & SSL-Certificates)
### Virtual-Host directives must be located in "/etc/apache2/" so that certbot can find them
COPY httpd.conf /usr/local/apache2/conf/httpd.conf
COPY httpd-umlp.conf /usr/local/apache2/conf/httpd-umlp.conf
COPY httpd-virthost-80.conf /etc/apache2/sites-available/httpd-virthost-80.conf

RUN a2dissite 000-default.conf
RUN a2ensite httpd-virthost-80.conf

# By default there are two configuration-location in the  "httpd" dockerimage:
#  1) /usr/local/apache2  AND \
#  2) /etc/apache2 \
#certbot (lets encrypt) is using the second one, the normale "httpd-foreground" command the first. \
#So we overwrite one configuration with a link to the other
RUN echo "Include /usr/local/apache2/conf/httpd.conf" > /etc/apache2/apache2.conf

# Test that the configuration is valid
RUN apachectl configtest -d /usr/local/apache2


# The actual content of the website
COPY dist/stanley /var/www/html

# get an ssl certificate and start the server
# the commands starts apache in the background, hence the "sleep infinity"
CMD certbot --apache --agree-tos --no-bootstrap -n -m ${EMAIL} -d ${UMLP_DEPLOYED_DOMAIN} ; sleep infinity
EXPOSE 80 443
