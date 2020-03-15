module Neo4jSakila
  class Address
    include Neo4j::ActiveNode

    property :address_id
    property :address
    property :address2
    #property :city_id
    property :district
    property :postal_code
    property :phone
    property :last_update

    has_one :out, :city, type: :city, model_class: "Neo4jSakila::City"
    has_one :in, :model, type: :false, model_class: [
      "Neo4jSakila::Customer",
      "Neo4jSakila::Store",
      "Neo4jSakila::Staff"
    ]
  end
end
