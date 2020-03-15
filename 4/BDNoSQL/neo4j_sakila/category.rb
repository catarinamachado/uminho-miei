module Neo4jSakila
  class Category
    include Neo4j::ActiveNode

    property :category_id
    property :name
    property :last_update

    has_many :in, :film, type: :film, model_class: "Neo4jSakila::Film"
  end
end
