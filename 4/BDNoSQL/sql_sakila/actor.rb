module SqlSakila
  class Actor < ActiveRecord::Base
    self.table_name = "actor"
    self.primary_key = "actor_id"

    has_many :film_actor, class_name: "FilmActor", foreign_key: :actor_id
    has_many :film, class_name: "Film", through: :film_actor
  end
end
