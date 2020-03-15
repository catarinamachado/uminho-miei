class ForceCreateNeo4jSakilaFilmUuidConstraint < Neo4j::Migrations::Base
  def up
    add_constraint :"Neo4jSakila::Film", :uuid, force: true
  end

  def down
    drop_constraint :"Neo4jSakila::Film", :uuid
  end
end
