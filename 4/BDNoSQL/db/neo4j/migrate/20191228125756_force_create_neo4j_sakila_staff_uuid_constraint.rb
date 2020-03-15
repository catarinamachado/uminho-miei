class ForceCreateNeo4jSakilaStaffUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Staff", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Staff", :uuid
  end
end
