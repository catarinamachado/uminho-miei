module SqlSakila
  class City < ActiveRecord::Base
    self.table_name = "city"
    self.primary_key = "city_id"

    belongs_to :country, class_name: "Country"
  end
end
