module Neo4jSakila
  class Rental
    include Neo4j::ActiveNode

    property :rental_id
    property :rental_date
    #property :inventory_id
    #property :customer_id
    property :return_date
    #property :staff_id
    property :last_update

    has_one :out, :customer, type: :customer, model_class: "Neo4jSakila::Customer"
    has_one :out, :staff, type: :staff, model_class: "Neo4jSakila::Staff"
    has_one :in, :film, rel_class: "Neo4jSakila::Inventory"

    has_many :in, :payment, type: :payment, model_class: "Neo4jSakila::Payment"
  end
end
