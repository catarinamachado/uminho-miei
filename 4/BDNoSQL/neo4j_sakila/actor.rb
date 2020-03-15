module Neo4jSakila
  class Actor
    include Neo4j::ActiveNode

    property :actor_id
    property :first_name
    property :last_name
    property :last_update

    has_many :out, :film, type: :film, model_class: "Neo4jSakila::Film"
  end
end
