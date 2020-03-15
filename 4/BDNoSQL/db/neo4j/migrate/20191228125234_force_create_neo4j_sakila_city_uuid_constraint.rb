class ForceCreateNeo4jSakilaCityUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::City", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::City", :uuid
  end
end
