FROM iamdevopstrainer/tomcat:base
COPY abc_technologies.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]