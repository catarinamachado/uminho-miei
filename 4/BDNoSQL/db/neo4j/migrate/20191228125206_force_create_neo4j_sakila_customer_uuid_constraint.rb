class ForceCreateNeo4jSakilaCustomerUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Customer", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Customer", :uuid
  end
end
