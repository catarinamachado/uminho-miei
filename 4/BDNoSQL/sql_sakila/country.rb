module SqlSakila
  class Country < ActiveRecord::Base
    self.table_name = "country"
    self.primary_key = "country_id"
  end
end
