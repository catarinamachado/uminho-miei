//Top 5 dos clientes com mais alugueres
match (c:`Neo4jSakila::Customer`)<-[:rental]-(r:`Neo4jSakila::Rental`)
return c, count(r)
order by count(r) desc
limit 5;

//Top 10 dos clientes que gastam mais dinheiro
match (c:`Neo4jSakila::Customer`)<-[:payment]-(p:`Neo4jSakila::Payment`)
return c, sum(toInteger(p.amount))
order by sum(toInteger(p.amount)) desc
limit 10;

// Rank mais alugueres por loja
match (s:`Neo4jSakila::Store`)<-[:customer]-(c:`Neo4jSakila::Customer`)<-[:payment]-(p:`Neo4jSakila::Payment`)
return s,count(p)
order by count(p) desc;

// Rank loja com mais faturamento
match (s:`Neo4jSakila::Store`)<-[:customer]-(c:`Neo4jSakila::Customer`)<-[:payment]-(p:`Neo4jSakila::Payment`)
return s,sum(toInteger(p.amount))
order by sum(toInteger(p.amount)) desc;

// Rank loja com mais filmes
match (s:`Neo4jSakila::Store`)<-[:film]-(f:`Neo4jSakila::Film`)
return s, count(f)
order by count(f) desc;
