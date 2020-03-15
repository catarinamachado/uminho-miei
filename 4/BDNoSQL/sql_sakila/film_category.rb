module SqlSakila
  class FilmCategory < ActiveRecord::Base
    self.table_name = "film_category"
    self.primary_keys = :film_id, :category_id

    belongs_to :film, class_name: "Film"
    belongs_to :category, class_name: "Category"
  end
end
