class ForceCreateNeo4jSakilaStoreUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Store", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Store", :uuid
  end
end
