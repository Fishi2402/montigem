FROM httpd:2.4-alpine
MAINTAINER "Software Engineering, RWTH Aachen University"

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD wget localhost:8080 -q -O - > /dev/null 2>&1

## Apache Configuration (e.g. Proxy to backend & SSL-Certificates)
COPY httpd.conf /usr/local/apache2/conf/httpd.conf
COPY httpd-umlp.conf /usr/local/apache2/conf/httpd-umlp.conf

## created from `npm run build`
COPY dist/stanley /var/www/html

EXPOSE 80 443
