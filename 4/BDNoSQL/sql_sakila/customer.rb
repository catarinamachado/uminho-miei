module SqlSakila
  class Customer < ActiveRecord::Base
    self.table_name = "customer"
    self.primary_key = "customer_id"

    belongs_to :address, class_name: "Address"
    belongs_to :store, class_name: "Store"

    has_many :rental, class_name: "Rental", foreign_key: :customer_id
    has_many :payment, class_name: "Payment", foreign_key: :customer_id
  end
end
