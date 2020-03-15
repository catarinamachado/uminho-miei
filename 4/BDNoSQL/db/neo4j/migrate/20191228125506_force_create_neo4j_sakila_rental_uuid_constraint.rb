class ForceCreateNeo4jSakilaRentalUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Rental", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Rental", :uuid
  end
end
