module SqlSakila
  class Staff < ActiveRecord::Base
    self.table_name = "staff"
    self.primary_key = "staff_id"

    belongs_to :address, class_name: "Address"

    has_many :rental, class_name: "Rental", foreign_key: :staff_id
  end
end
