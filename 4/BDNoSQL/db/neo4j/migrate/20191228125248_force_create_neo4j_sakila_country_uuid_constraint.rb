class ForceCreateNeo4jSakilaCountryUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Country", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Country", :uuid
  end
end
