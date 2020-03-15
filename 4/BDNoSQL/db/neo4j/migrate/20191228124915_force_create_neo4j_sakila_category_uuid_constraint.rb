class ForceCreateNeo4jSakilaCategoryUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Category", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Category", :uuid
  end
end
