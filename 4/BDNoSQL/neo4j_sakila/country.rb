module Neo4jSakila
  class Country
    include Neo4j::ActiveNode

    property :country_id
    property :country
    property :last_update

    has_many :in, :city, type: :city, model_class: "Neo4jSakila::City"
  end
end
