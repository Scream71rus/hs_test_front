#!/bin/sh

mv $(lein release | sed -n 's/^Created \(.*standalone\.jar\)/\1/p') app-standalone.jar
$(java -jar app-standalone.jar)