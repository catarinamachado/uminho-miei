module Neo4jSakila
  class Payment
    include Neo4j::ActiveNode

    property :payment_id
    #property :customer_id
    #property :staff_id
    #property :rental_id
    property :amount
    property :payment_date
    property :last_update

    has_one :out, :customer, type: :customer, model_class: "Neo4jSakila::Customer"
    has_one :out, :staff, type: :staff, model_class: "Neo4jSakila::Staff"
    has_one :out, :rental, type: :rental, model_class: "Neo4jSakila::Rental"
  end
end
