module Neo4jSakila
  class Language
    include Neo4j::ActiveNode

    property :language_id
    property :name
    property :last_update

    has_many :in, :film, type: :film, model_class: "Neo4jSakila::Film"
  end
end
