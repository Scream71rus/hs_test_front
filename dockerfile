FROM clojure

RUN apt-get update && apt-get upgrade

RUN apt-get install curl  -y
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash
RUN apt-get install nodejs -y

WORKDIR /usr/src/app
COPY . /usr/src/app

RUN lein clean
RUN lein release


FROM nginx

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=0 /usr/src/app/resources/public /usr/src/app/resources/public
