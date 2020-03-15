module SqlSakila
  class Address < ActiveRecord::Base
    self.table_name = "address"
    self.primary_key = "address_id"

    belongs_to :city, class_name: "City"
  end
end
