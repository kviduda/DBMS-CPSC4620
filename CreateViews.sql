USE Pizzeria;

CREATE OR REPLACE VIEW ToppingPopularity AS SELECT topping.Topping_Name as Topping, 
count(pizza_topping.Topping_ID) + coalesce(sum(pizza_topping.Is_Double),0) as ToppingCount
from pizza_topping 
right join topping on pizza_topping.Topping_ID = topping.Topping_ID
group by topping.Topping_Name
order by ToppingCount desc;

select * from ToppingPopularity; 

create or replace view ProfitByPizza AS select baseprice.Pizza_Size as 'Size',
baseprice.Pizza_CrustType as 'Crust',
round(sum(pizza.Pizza_PriceToCustomer-pizza.Pizza_PriceToBusiness), 2) as 'Profit',
MAX(ordertable.Order_TimeStamp) as 'LastOrderDate' from baseprice 
right join pizza on baseprice.Pizza_Size = pizza.Pizza_Size and baseprice.Pizza_CrustType = pizza.Pizza_CrustType
join ordertable on pizza.Order_ID = ordertable.Order_ID
group by baseprice.Pizza_Size, baseprice.Pizza_CrustType
order by Profit desc;

select * from ProfitByPizza; 

create or replace view ProfitByOrder as select ordertable.Order_Type as 'customerType', 
date_format(ordertable.Order_TimeStamp, '%c/%Y') as 'OrderMonth',
round(sum(ordertable.Order_PriceToCustomer), 2) as 'TotalOrderPrice',
round(sum(ordertable.Order_PriceToBusiness), 2) as 'TotalOrderCost',
round((sum(ordertable.Order_PriceToCustomer)-sum(ordertable.Order_PriceToBusiness)), 2) as 'Profit'
from ordertable
group by customerType, OrderMonth
union select ' ', 'Grand Total',
round(sum(ordertable.Order_PriceToCustomer), 2),
round(sum(ordertable.Order_PriceToBusiness), 2),
round((sum(ordertable.Order_PriceToCustomer)-sum(ordertable.Order_PriceToBusiness)), 2) 
from ordertable;

select * from ProfitByOrder;