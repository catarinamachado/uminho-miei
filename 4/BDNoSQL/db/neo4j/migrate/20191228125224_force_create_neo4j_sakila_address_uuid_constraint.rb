class ForceCreateNeo4jSakilaAddressUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Address", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Address", :uuid
  end
end
