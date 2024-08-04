FROM eclipse-temurin:21-jre

COPY build/ /home/tpl
WORKDIR /home/tpl

COPY build/start.sh start.sh

RUN chmod +x start.sh

#ENTRYPOINT ["tail", "-f", "/dev/null"]
ENTRYPOINT ["/home/tpl/start.sh"]