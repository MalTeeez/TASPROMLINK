FROM eclipse-temurin:21-jre


RUN apt-get update && apt-get install --assume-yes --no-install-recommends --no-install-suggests gnupg2

RUN echo "deb http://archive.raspberrypi.com/debian/ bookworm main" | tee -a /etc/apt/sources.list
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 82B129927FA3303E

RUN apt-get update && apt-get install --assume-yes --no-install-recommends --no-install-suggests raspi-utils-core


COPY build/ /home/tpl
WORKDIR /home/tpl
RUN chmod +x start.sh

#ENTRYPOINT ["tail", "-f", "/dev/null"]
ENTRYPOINT ["bash", "/home/tpl/start.sh"]