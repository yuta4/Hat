#!/usr/bin/env bash

profile=$1

java -Dspring.profiles.active=${profile} -jar hat-online-0.0.1-SNAPSHOT.jar