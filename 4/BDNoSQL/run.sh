#!/bin/sh
mongo sakila --eval "db.dropDatabase()"
mongo --eval "db.getSiblingDB('sakila')"

ruby main.rb
