module SqlSakila
  class Rental < ActiveRecord::Base
    self.table_name = "rental"
    self.primary_key = "rental_id"

    belongs_to :inventory, class_name: "Inventory"
    belongs_to :customer, class_name: "Customer"
    belongs_to :staff, class_name: "Staff"
  end
end
