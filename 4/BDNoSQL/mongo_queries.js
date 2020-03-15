//titulo dos filmes que utilizador "MARY SMITH" alugou.(basta mudar o nome para qualquer outro utilizador)
db.customers.find({"first_name":"MARY", "last_name": "SMITH"},{"_id":0,"film.title":1}).pretty();

//quantidade de filmes que utilizador "MARY SMITH" alugou
db.customers.aggregate(
    {$match: {"first_name": "MARY", "last_name":"SMITH"}},
    {$unwind: "$film"},
    {$group: {_id:null, number:{$sum:1}}
});

//todos os filmes em que o ator participou
db.actor.find({"first_name":"PENELOPE", "last_name": "GUINESS"},{"_id":0,"film.title":1}).pretty();

//morada a da loja a que o utilizador "MARY SMITH" pertence
db.store.find({"customer.first_name":"MARY", "customer.last_name": "SMITH"},{"address":1}).pretty();

//todos os atores do filme
db.film.find({"title":"ACADEMY DINOSAUR"},{"_id":0, "actor.first_name":1, "actor.last_name":1}).pretty();