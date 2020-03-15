module SqlSakila
  class Category < ActiveRecord::Base
    self.table_name = "category"
    self.primary_key = "category_id"
  end
end
