module SqlSakila
  class Store < ActiveRecord::Base
    self.table_name = "store"
    self.primary_key = "store_id"

    belongs_to :address, class_name: "Address"
    belongs_to :manager_staff, class_name: "Staff"

    has_many :staff, class_name: "Staff", foreign_key: :store_id
    has_many :inventory, class_name: "Inventory", foreign_key: :store_id
    has_many :customer, class_name: "Customer", foreign_key: :store_id
  end
end
