FROM clojure

RUN apt-get update && apt-get upgrade && \
    apt-get install postgresql postgresql-contrib -y

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app

RUN chmod +x ./docker-entrypoint.sh
ENTRYPOINT ["/usr/src/app/docker-entrypoint.sh"]