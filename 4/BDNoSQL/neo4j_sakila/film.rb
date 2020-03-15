module Neo4jSakila
  class Film
    include Neo4j::ActiveNode

    property :film_id
    property :title
    property :description
    property :release_year
    #property :language_id
    #property :original_language_id
    property :rental_duration
    property :rental_rate
    property :length
    property :replacement_cost
    property :rating
    property :special_features
    property :last_update

    has_many :in, :actor, type: :actor, model_class: "Neo4jSakila::Actor"
    has_many :out, :category, type: :category, model_class: "Neo4jSakila::Category"
    has_many :out, :rental, rel_class: "Neo4jSakila::Inventory"
    has_many :out, :store, type: :store, model_class: "Neo4jSakila::Store"

    has_one :out, :language, type: :language, model_class: "Neo4jSakila::Language"
    has_one :out, :original_language, type: :original_language, model_class: "Neo4jSakila::Language"
  end
end
