module Neo4jSakila
  class Inventory
    include Neo4j::ActiveRel

    from_class "Neo4jSakila::Film"
    to_class   "Neo4jSakila::Rental"

    property :store_id, type: Integer
  end
end
