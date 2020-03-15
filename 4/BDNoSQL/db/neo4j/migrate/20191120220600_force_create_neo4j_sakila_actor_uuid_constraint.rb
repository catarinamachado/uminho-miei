class ForceCreateNeo4jSakilaActorUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Actor", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Actor", :uuid
  end
end
