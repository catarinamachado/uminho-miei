module Neo4jSakila
  class City
    include Neo4j::ActiveNode

    property :city_id
    property :city
    #property :country_id
    property :last_update

    has_one :out, :country, type: :country, model_class: "Neo4jSakila::Country"
    has_many :in, :address, type: :address, model_class: "Neo4jSakila::Address"
  end
end
