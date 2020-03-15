module Neo4jSakila
  class Customer
    include Neo4j::ActiveNode

    property :customer_id
    #property :store_id
    property :first_name
    property :last_name
    property :email
    #property :address_id
    property :active
    property :create_date
    property :last_update

    has_many :in, :rental, type: :rental, model_class: "Neo4jSakila::Rental"
    has_many :in, :payment, type: :payment, model_class: "Neo4jSakila::Payment"    

    has_one :out, :store, type: :store, model_class: "Neo4jSakila::Store"
    has_one :out, :address, type: :address, model_class: "Neo4jSakila::Address"
  end
end
