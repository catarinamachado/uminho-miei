require "active_record"
require "composite_primary_keys"
require "mongo"
require "neo4j/core/cypher_session/adaptors/http"
require "neo4j"

Dir["./sql_sakila/*.rb"].each { |file| require file }

require "./mongo_sakila"

Dir["./neo4j_sakila/*.rb"].each { |file| require file }
require "./neo4j_sakila"

ActiveRecord::Base.establish_connection(
  adapter: "mysql2",
  host: "localhost",
  username: "root",
  password: "",
  database: "sakila"
)

# neo4j
neo4j_adaptor = Neo4j::Core::CypherSession::Adaptors::HTTP.new("http://neo4j@localhost:7474")
Neo4j::ActiveBase.on_establish_session { Neo4j::Core::CypherSession.new(neo4j_adaptor) }

mski = MongoSakila.new
mski.collect_actors
mski.collect_films
mski.collect_stores
mski.collect_customers

Neo4jSakila.cleanup
Neo4jSakila.collect_models
Neo4jSakila.create_edges
