class ForceCreateNeo4jSakilaLanguageUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Language", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Language", :uuid
  end
end
