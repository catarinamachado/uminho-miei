module Neo4jSakila
  class Store 
    include Neo4j::ActiveNode

    property :store_id
    #property :manager_staff_id
    #property :address_id
    property :last_update

    has_one :out, :address, type: :address, model_class: "Neo4jSakila::Address"
    has_one :out, :manager_staff, type: :manager_staff, model_class: "Neo4jSakila::Staff"

    has_many :in, :staff, type: :staff, model_class: "Neo4jSakila::Staff"
    has_many :in, :film, type: :film, model_class: "Neo4jSakila::Film"
    has_many :in, :customer, type: :customer, model_class: "Neo4jSakila::Customer"
  end
end
