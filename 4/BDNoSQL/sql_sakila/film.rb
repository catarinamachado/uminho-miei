module SqlSakila
  class Film < ActiveRecord::Base
    self.table_name = "film"
    self.primary_key = "film_id"

    has_many :film_actor, class_name: "FilmActor", foreign_key: :film_id
    has_many :film_category, class_name: "FilmCategory", foreign_key: :film_id
    has_many :category, class_name: "Category", through: :film_category
    has_many :actor, class_name: "Actor", foreign_key: :actor_id
    has_many :inventory, class_name: "Inventory", foreign_key: :film_id

    belongs_to :language, class_name: "Language"
    belongs_to :original_language, class_name: "Language", optional: true
  end
end
