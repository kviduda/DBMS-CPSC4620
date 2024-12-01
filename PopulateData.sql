USE Pizzeria;
INSERT INTO `topping`
(
`Topping_Name`, 
`Topping_PriceToCustomer`, 
`Topping_PriceToBusiness`, 
`Topping_CurInvLevel`,
`Topping_MinInvLevel`, 
`Topping_QuantityForPersonal`,
`Topping_QuantityForMedium`,
`Topping_QuantityForLarge`,
`Topping_QuantityForXLarge`
)
VALUES('Pepperoni', 1.25, 0.2, 100, 50, 2, 2.75, 3.5, 4.5),
 ('Sausage', 1.25, 0.15, 100, 50, 2.5, 3, 3.5, 4.25),
 ('Ham', 1.5, 0.15, 78, 25, 2, 2.5, 3.25, 4),
 ('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),
 ('Green Pepper', 0.5, 0.02, 79, 25, 1, 1.5, 2, 2.5),
 ('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75),
 ('Roma Tomato', 0.75, 0.03, 86, 10, 2, 3, 3.5, 4.5),
 ('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3),
 ('Black Olives', 0.6, 0.1, 39, 25, 0.75, 1, 1.5, 2),
 ('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2),
 ('Jalapenos', 0.5, 0.05, 64, 0, 0.5, 0.75, 1.25, 1.75),
 ('Banana Peppers', 0.5, 0.05, 36, 0, 0.6, 1, 1.3, 1.75),
 ('Regular Cheese', 1.5, 0.12, 250, 50, 2, 3.5, 5, 7),
 ('Four Cheese Blend', 2, 0.15, 150, 25, 2, 3.5, 5, 7),
 ('Feta Cheese', 2, 0.18, 75, 0, 1.75, 3, 4, 5.5),
 ('Goat Cheese', 2, 0.2, 54, 0, 1.6, 2.75, 4, 5.5),
 ('Bacon', 1.5, 0.25, 89, 0, 1, 1.5, 2, 3);
 
 INSERT INTO discount(Discount_Name, Percentage_Off, Dollar_Off) 
 VALUES("Employee", true, 15), 
 ("Lunch Special Medium", false, 1),
 ("Lunch Special Large", false, 2),
 ("Specialty Pizza", false, 1.50),
 ("Happy Hour", true, 10),
 ("Gameday Special", true, 20);

INSERT INTO baseprice(Pizza_Size, Pizza_CrustType, Baseprice_ToCustomer,Baseprice_ToBusiness)
VALUES('Small', 'Thin', 3, 0.5),
('Small', 'Original', 3, 0.75),
('Small', 'Pan', 3.5, 1),
('Small', 'Gluten-Free', 4, 2),
('Medium', 'Thin', 5, 1),
('Medium', 'Original', 5, 1.5),
('Medium', 'Pan', 6, 2.25),
('Medium', 'Gluten-Free', 6.25, 3),
('Large', 'Thin', 8, 1.25),
('Large', 'Original', 8, 2),
('Large', 'Pan', 9, 3),
('Large', 'Gluten-Free', 9.5, 4),
('XLarge', 'Thin', 10, 2),
('XLarge', 'Original', 10, 3),
('XLarge', 'Pan', 11.5, 4.5),
('XLarge', 'Gluten-Free', 12.5, 6);

insert into customer(Customer_FName, Customer_LName, Customer_Phone,Customer_Street,Customer_City,Customer_State,Customer_Zipcode) 
values('Andrew', 'Wilkes-Krier', '8642545861','115 Party Blvd', 'Anderson','SC','29621'),
('Matt', 'Engers', '8644749953',null,null,null,null),
('Frank', 'Turner', '8642328944','6745 Wessex St', 'Anderson','SC','29621'), 
('Milo', 'Auckerman', '8648785679','8879 Suburban Home', 'Anderson', 'SC', '29621');

insert into ordertable(Customer_ID, Order_TimeStamp, Order_PriceToCustomer, Order_PriceToBusiness, Order_Type,Order_IsComplete) 
values(NULL, '2023-03-05 12:03:00', 20.75, 3.68, 'dinein',1),
(NULL, '2023-04-03 12:05:00', 19.78, 4.63,'dinein',1),
(1, '2023-03-03 21:30:00', 89.28, 19.80, 'pickup',1),
(1, '2023-04-20 19:11:00', 86.19, 23.62, 'delivery',1),
(2, '2023-03-02 17:30:00', 27.45, 7.88, 'pickup',1),
(3, '2023-03-02 18:17:00', 25.81, 4.24, 'delivery',1),
(4, '2023-04-13 20:32:00', 37.25, 6, 'delivery',1),
(NULL, '2023-12-08 12:03:00', 20.75, 3.68, 'dinein',0);


insert into order_discount(Order_ID, Discount_ID) 
values(1, 3), (4, 6), (7, 1);

insert into pizza(Order_ID, Pizza_PriceToCustomer, Pizza_PriceToBusiness, Pizza_Status, Pizza_Size,Pizza_CrustType) 
values(1, 20.75, 3.68, 'processing', 'Large', 'Thin'), 
(2, 12.85, 3.23, 'processing', 'Medium', 'Pan'),
(2, 6.93, 1.40, 'processing', 'Small', 'Original'),
(3, 14.88, 3.30, 'processing', 'Large', 'Original'),
(3, 14.88, 3.30, 'processing', 'Large', 'Original'),
(3, 14.88, 3.30, 'processing', 'Large', 'Original'),
(3, 14.88, 3.30, 'processing', 'Large', 'Original'),
(3, 14.88, 3.30, 'processing', 'Large', 'Original'),
(3, 14.88, 3.30, 'processing', 'Large', 'Original'),
(4, 27.94, 9.19, 'processing', 'XLarge', 'Original'),
(4, 31.50, 6.25, 'processing', 'XLarge', 'Original'),
(4, 26.75, 8.18, 'processing', 'XLarge', 'Original'),
(5, 27.45, 7.88, 'processing', 'XLarge', 'Gluten-Free'),
(6, 25.81, 4.24, 'processing', 'Large', 'Thin'),
(7, 18.0, 2.75, 'processing', 'Large', 'Thin'),
(7, 19.25, 3.25, 'processing', 'Large', 'Thin');

insert into pizza_discount(Pizza_ID, Discount_ID) 
values(2, 2), (3, 4), (11, 4), (13, 4); 

insert into dinein (Order_ID, Table_Num)
values(1, 21), (2, 4);

insert into pickup(Order_ID,Customer_ID,Order_IsPicked) 
values(3,1,1),(5,2,1);

insert into delivery (Order_ID,Customer_ID,Order_IsDelivered)
values(4,1,1),
(6,3,1),
(7,4,1);

insert into pizza_topping(Pizza_ID, Topping_ID,Is_Double)
values(1, 13, 1), (1, 1, 0), (1, 2, 0), 
(2, 15, 0), (2, 9, 0), (2, 7, 0), (2, 8, 0), (2, 12, 0), 
(3, 13, 0), (3, 4, 0), (3, 12, 0), 
(4, 13, 0), (4, 1, 0), 
(5, 13, 0), (5, 1, 0),
(6, 13, 0), (6, 1, 0),
(7, 13, 0), (7, 1, 0),
(8, 13, 0), (8, 1, 0),
(9, 13, 0), (9, 1, 0),
(10, 1, 0), (10, 2, 0),(10, 14, 0),
(11, 3, 1), (11, 10, 1), (11, 14, 0),
(12, 14, 0), (12, 17, 0), (12, 4, 0),
(13, 5, 0), (13, 6, 0), (13, 7, 0), (13, 8, 0), (13, 9, 0), (13, 16, 0),
(14, 4, 0), (14, 5, 0), (14, 6, 0), (14, 8, 0), (14, 14, 1),
(15, 14, 1),
(16, 13, 0), (16, 1, 1);