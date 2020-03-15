module SqlSakila
  class Payment < ActiveRecord::Base
    self.table_name = "payment"
    self.primary_key = "payment_id"

    belongs_to :customer, class_name: "Customer"
    belongs_to :staff, class_name: "Staff"
    belongs_to :rental, class_name: "Rental", optional: true
  end
end
