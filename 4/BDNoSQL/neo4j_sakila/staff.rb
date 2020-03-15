module Neo4jSakila
  class Staff 
    include Neo4j::ActiveNode

    property :staff_id
    property :first_name
    property :last_name
    #property :address_id
    property :email
    #property :store_id
    property :active
    property :username
    property :password
    property :last_update

    has_one :out, :address, type: :address, model_class: "Neo4jSakila::Address"
    has_many :in, :rental, type: :rental, model_class: "Neo4jSakila::Rental"
  end
end
