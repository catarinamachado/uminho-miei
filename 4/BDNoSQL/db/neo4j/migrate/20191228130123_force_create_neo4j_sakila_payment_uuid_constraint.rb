class ForceCreateNeo4jSakilaPaymentUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Payment", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Payment", :uuid
  end
end
