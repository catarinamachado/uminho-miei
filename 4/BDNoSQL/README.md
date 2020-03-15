# Bases de Dados NoSQL

* Relatório [aqui](Report.pdf).

### Setup
gem install bundler
bundle install
rake neo4j:install
rake neo4j:migrate

### Services
rake neo4j:start
mysql.server start
mongod --config /usr/local/etc/mongod.conf

### Run
sh run.sh


