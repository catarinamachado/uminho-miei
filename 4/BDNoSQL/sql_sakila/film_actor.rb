module SqlSakila
  class FilmActor < ActiveRecord::Base
    self.table_name = "film_actor"
    self.primary_keys = :actor_id, :film_id

    belongs_to :actor, class_name: "Actor"
    belongs_to :film, class_name: "Film"
  end
end
