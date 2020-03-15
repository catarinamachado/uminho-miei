class MongoSakila
  def initialize
    @mongo_client = Mongo::Client.new([ "127.0.0.1:27017" ], database: "sakila")
    #mongo_db = mongo_client.database
  end

  def collect_actors
    collection = @mongo_client[:actor]
    actors = SqlSakila::Actor.includes(film: :category).as_json(include: {film: {include: [:category]}})
    actors.each do |actor|
      actor["film"].each do |film|
        film["category"].each do |category|
          category.delete("category_id")
        end
        film["language"] = SqlSakila::Film.find(film["film_id"]).language.as_json(except: [:language_id])
        ol = SqlSakila::Film.find(film["film_id"]).original_language
        film["original_language"] = ol.as_json(except: [:language_id]) if ol
        film.delete("film_id")
        film.delete("language_id")
        film.delete("original_language_id")
      end
      actor.delete("actor_id")
      collection.insert_one(actor)
    end
  end

  def collect_films
    collection = @mongo_client[:film]
    films = SqlSakila::Film.includes(:actor, :category).as_json(include: [:actor, :category])
    films.each do |film|
      film["actor"].each do |actor|
        actor.delete("actor_id")
      end
      film["category"].each do |category|
        category.delete("category_id")
      end
      film["language"] = SqlSakila::Film.find(film["film_id"]).language.as_json(except: [:language_id])
      ol = SqlSakila::Film.find(film["film_id"]).original_language
      film["original_language"] = ol.as_json(except: [:language_id]) if ol
      film.delete("language_id")
      film.delete("original_language_id")
      film.delete("film_id")
      collection.insert_one(film)
    end
  end

  def collect_stores
    collection = @mongo_client[:store]
    stores = SqlSakila::Store.all
    stores.each do |store|
      store_json = {}
      store_json["store_id"] = store.store_id
      store_json["last_update"] = store.last_update

      store_json["address"] = store.address.as_json
      store_json["address"]["city"] = store.address.city.as_json
      store_json["address"]["city"]["country"] = store.address.city.country.as_json
      store_json["address"]["city"]["country"].delete("country_id")
      store_json["address"]["city"].delete("country_id")
      store_json["address"].delete("city_id")
      store_json["address"].delete("location")
      store_json["address"].delete("address_id")
      store_json.delete("address_id")

      manager_staff_json = store.manager_staff.as_json
      manager_staff_json["address"] = store.manager_staff.address.as_json
      manager_staff_json["address"]["city"] = store.manager_staff.address.city.as_json
      manager_staff_json["address"]["city"]["country"] = store.manager_staff.address.city.country.as_json
      manager_staff_json["address"]["city"]["country"].delete("country_id")
      manager_staff_json["address"]["city"].delete("country_id")
      manager_staff_json["address"].delete("city_id")
      manager_staff_json["address"].delete("location")
      manager_staff_json["address"].delete("address_id")
      manager_staff_json.delete("address_id")
      manager_staff_json.delete("picture")
      store_json["manager_staff"] = manager_staff_json

      staff_list_json = []
      store.staff.each do |staff|
        staff_json = staff.as_json
        staff_json["address"] = staff.address.as_json
        staff_json["address"]["city"] = staff.address.city.as_json
        staff_json["address"]["city"]["country"] = staff.address.city.country.as_json
        staff_json["address"]["city"]["country"].delete("country_id")
        staff_json["address"]["city"].delete("country_id")
        staff_json["address"].delete("city_id")
        staff_json["address"].delete("location")
        staff_json["address"].delete("address_id")
        staff_json.delete("address_id")
        staff_json.delete("picture")
        staff_list_json.push(staff_json)
      end
      store_json["staff"] = staff_list_json

      customer_list_json = []
      store.customer.each do |customer|
        customer_json = customer.as_json
        customer_json["address"] = customer.address.as_json
        customer_json["address"]["city"] = customer.address.city.as_json
        customer_json["address"]["city"]["country"] = customer.address.city.country.as_json
        customer_json["address"]["city"]["country"].delete("country_id")
        customer_json["address"]["city"].delete("country_id")
        customer_json["address"].delete("city_id")
        customer_json["address"].delete("location")
        customer_json["address"].delete("address_id")
        customer_json.delete("address_id")

        customer_film_list_json = []
        customer.rental.each do |rental|
          film_json = SqlSakila::Film.includes(:actor, :category).find(rental.inventory.film.id).as_json(include: [:actor, :category])
          film_json["actor"].each do |actor|
            actor.delete("actor_id")
          end
          film_json["category"].each do |category|
            category.delete("category_id")
          end
          film_json["language"] = SqlSakila::Film.find(film_json["film_id"]).language.as_json(except: [:language_id])
          ol = SqlSakila::Film.find(film_json["film_id"]).original_language
          film_json["original_language"] = ol.as_json(except: [:language_id]) if ol
          film_json.delete("language_id")
          film_json.delete("original_language_id")
          film_json.delete("film_id")

          customer_film_list_json.push(film_json)
        end
        customer_json["film"] = customer_film_list_json

        customer_list_json.push(customer_json)
      end
      store_json["customer"] = customer_list_json
      collection.insert_one(store_json)
    end
  end

  def collect_customers
    collection = @mongo_client[:customers]
    customers = SqlSakila::Customer.all
    customers.each do |customer|
      customer_json = customer.as_json
      customer_json["address"] = customer.address.as_json
      customer_json["address"]["city"] = customer.address.city.as_json
      customer_json["address"]["city"]["country"] = customer.address.city.country.as_json
      customer_json["address"]["city"]["country"].delete("country_id")
      customer_json["address"]["city"].delete("country_id")
      customer_json["address"].delete("city_id")
      customer_json["address"].delete("location")
      customer_json["address"].delete("address_id")
      customer_json.delete("address_id")

      customer_film_list_json = []
      customer.rental.each do |rental|
        film_json = SqlSakila::Film.includes(:actor, :category).find(rental.inventory.film.id).as_json(include: [:actor, :category])
        film_json["actor"].each do |actor|
          actor.delete("actor_id")
        end
        film_json["category"].each do |category|
          category.delete("category_id")
        end
        film_json["language"] = SqlSakila::Film.find(film_json["film_id"]).language.as_json(except: [:language_id])
        ol = SqlSakila::Film.find(film_json["film_id"]).original_language
        film_json["original_language"] = ol.as_json(except: [:language_id]) if ol
        film_json.delete("language_id")
        film_json.delete("original_language_id")
        film_json.delete("film_id")

        customer_film_list_json.push(film_json)
      end
      customer_json["film"] = customer_film_list_json
      collection.insert_one(customer_json)
    end
  end
end
