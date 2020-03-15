module SqlSakila
  class Inventory < ActiveRecord::Base
    self.table_name = "inventory"
    self.primary_key = "inventory_id"

    belongs_to :film, class_name: "Film"
    belongs_to :store, class_name: "Store"

    has_many :rental, class_name: "Rental", foreign_key: :inventory_id
  end
end
