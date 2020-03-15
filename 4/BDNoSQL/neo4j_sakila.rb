module Neo4jSakila
  class << self
    def cleanup
      Neo4j::ActiveBase.current_session.query("MATCH (n) DETACH DELETE n")
    end

    def collect_models
      SqlSakila::Actor.all.each do |actor|
        Actor.create(actor.as_json)
      end

      SqlSakila::Film.all.each do |film|
        Film.create(film.as_json(except: [:language_id, :original_language_id]))
      end

      SqlSakila::Language.all.each do |language|
        Language.create(language.as_json)
      end

      SqlSakila::Category.all.each do |category|
        Category.create(category.as_json)
      end

      SqlSakila::Customer.all.each do |customer|
        Customer.create(customer.as_json(except: [:store_id, :address_id]))
      end

      SqlSakila::Address.all.each do |address|
        Address.create(address.as_json(except: [:location, :city_id]))
      end

      SqlSakila::City.all.each do |city|
        City.create(city.as_json(except: [:country_id]))
      end

      SqlSakila::Country.all.each do |country|
        Country.create(country.as_json)
      end

      SqlSakila::Rental.all.each do |rental|
        Rental.create(rental.as_json(except: [:inventory_id, :customer_id, :staff_id]))
      end

      SqlSakila::Payment.all.each do |payment|
        Payment.create(payment.as_json(except: [:customer_id, :staff_id, :rental_id]))
      end

      SqlSakila::Store.all.each do |store|
        Store.create(store.as_json(except: [:manager_staff_id, :address_id]))
      end

      SqlSakila::Staff.all.each do |staff|
        Staff.create(staff.as_json(except: [:picture, :address_id, :store_id]))
      end
    end

    def create_edges
      SqlSakila::Actor.all.each do |actor|
        actor_n = Actor.find_by(actor_id: actor.actor_id)
        actor.film.each do |f|
          actor_n.film << Film.find_by(film_id: f.film_id)
        end
      end

      SqlSakila::Film.all.each do |film|
        film_n = Film.find_by(film_id: film.film_id)
        film.actor.each do |a|
          film_n.actor << Actor.find_by(actor_id: a.actor_id)
        end

        film.category.each do |c|
          cc = Category.find_by(category_id: c.category_id)
          film_n.category << cc
          cc.film << film_n
        end

        stores = Set.new
        film.inventory.each do |i|
          i.rental.each do |r|
            rental_n = Rental.find_by(rental_id: r.rental_id)
            s_id = r.inventory.store_id
            Inventory.create(
              from_node: film_n,
              to_node: rental_n,
              store_id: s_id
            )
            stores.add(s_id)
          end
        end

        stores.each do |s|
          ss = Store.find_by(store_id: s)
          film_n.store << ss
          ss.film << film_n
        end

        film_n.language = Language.find_by(language_id: film.language_id)
        film_n.original_language = Language.find_by(language_id: film.original_language_id)
      end

      SqlSakila::City.all.each do |city|
        city_n = City.find_by(city_id: city.city_id)
        city_n.country = Country.find_by(country_id: city.country_id)
      end

      SqlSakila::Address.all.each do |address|
        address_n = Address.find_by(address_id: address.address_id)
        address_n.city = City.find_by(city_id: address.city_id)
      end

      SqlSakila::Store.all.each do |store|
        store_n = Store.find_by(store_id: store.store_id)
        
        store_n.address = Address.find_by(address_id: store.address_id)
        store_n.manager_staff = Staff.find_by(staff_id: store.manager_staff_id)

        store.staff.each do |s|
          store_n.staff << Staff.find_by(staff_id: s.staff_id)
        end

        store.customer.each do |c|
          store_n.customer << Customer.find_by(customer_id: c.customer_id)
        end
      end

      SqlSakila::Customer.all.each do |customer|
        customer_n = Customer.find_by(customer_id: customer.customer_id)
        customer.rental.each do |r|
          rr = Rental.find_by(rental_id: r.rental_id)
          customer_n.rental << rr
          rr.customer = rental_n
        end

        customer.payment.each do |p|
          customer_n.payment << Payment.find_by(payment_id: p.payment_id)
        end

        customer_n.store = Store.find_by(store_id: customer.store_id)
        customer_n.address = Address.find_by(address_id: customer.address_id)
      end

      SqlSakila::Staff.all.each do |staff|
        staff_n = Staff.find_by(staff_id: staff.staff_id)
        staff_n.address = Address.find_by(address_id: staff.address_id)
        staff.rental do |r|
          rr = Rental.find_by(rental_id: r.rental_id)
          staff_n.rental << rr
          rr.staff = staff_n
        end
      end

      SqlSakila::Payment.all.each do |payment|
        payment_n = Payment.find_by(payment_id: payment.payment_id)
        payment_n.customer = Customer.find_by(customer_id: payment.customer_id)
        payment_n.staff = Staff.find_by(staff_id: payment.staff_id)
        rr = Rental.find_by(rental_id: payment.rental_id)
        payment_n.rental = rr
        rr.payment << rental_n
      end
    end
  end
end
