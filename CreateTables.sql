DROP DATABASE IF EXISTS Pizzeria;
CREATE DATABASE Pizzeria; 
USE Pizzeria;
CREATE TABLE customer
(
	Customer_ID int AUTO_INCREMENT,
    Customer_Fname varchar(255) NOT NULL,
    Customer_Lname varchar(255) NOT NULL,
    Customer_Phone varchar(255) NOT NULL,
    Customer_Street varchar(255) ,
    Customer_City varchar(255) ,
    Customer_State varchar(255) ,
    Customer_Zipcode varchar(255),
    PRIMARY KEY(Customer_ID)    
);


CREATE TABLE ordertable
(
	Order_ID int AUTO_INCREMENT,
    Customer_ID int,
    Order_TimeStamp datetime NOT NULL,
    Order_PriceToCustomer float NOT NULL,
    Order_PriceToBusiness float NOT NULL,
    Order_Type varchar(255) NOT NULL,
    Order_IsComplete int,
    PRIMARY KEY(Order_ID),
    FOREIGN KEY(Customer_ID) REFERENCES customer(Customer_ID)
);


CREATE TABLE dinein
(
	Order_ID int,
    Table_Num int NOT NULL,
    PRIMARY KEY(Order_ID),
    FOREIGN KEY(Order_ID) REFERENCES ordertable(Order_ID)
);

CREATE TABLE pickup
(
	Order_ID int,
    Customer_ID int,
    Order_IsPicked int,
    PRIMARY KEY(Order_ID),
    FOREIGN KEY(Order_ID) REFERENCES ordertable(Order_ID),
    FOREIGN KEY(Customer_ID) REFERENCES customer(Customer_ID)
);

CREATE TABLE delivery
(
	Order_ID int,
    Customer_ID int,
    Order_IsDelivered int,
    Customer_Address varchar(255),
    PRIMARY KEY(Order_ID),
    FOREIGN KEY(Order_ID) REFERENCES ordertable(Order_ID),
    FOREIGN KEY(Customer_ID) REFERENCES customer(Customer_ID)
);

CREATE TABLE discount
(
	Discount_ID int AUTO_INCREMENT,
    Discount_Name varchar(255) NOT NULL,
    Dollar_Off  boolean NOT NULL,
    Percentage_Off float NOT NULL,
    PRIMARY KEY(Discount_ID)
);

CREATE TABLE order_discount
(
	Order_ID int,
    Discount_ID int,
    PRIMARY KEY(Order_ID, Discount_ID),
    FOREIGN KEY(Order_ID) REFERENCES ordertable(Order_ID),
    FOREIGN KEY(Discount_ID) REFERENCES discount(Discount_ID)
);

CREATE TABLE baseprice
(
	Pizza_CrustType varchar(255),
    Pizza_Size varchar(255),
    Baseprice_ToCustomer float NOT NULL,
    Baseprice_ToBusiness float NOT NULL,
    PRIMARY KEY(Pizza_CrustType, Pizza_Size)
);

CREATE TABLE pizza
(
	Pizza_ID int AUTO_INCREMENT,
    Order_ID int NOT NULL,
    Pizza_PriceToBusiness float NOT NULL,
    Pizza_PriceToCustomer float NOT NULL,
    Pizza_Status varchar(255) NOT NULL,
    Pizza_CrustType varchar(255) NOT NULL,
    Pizza_Size varchar(255) NOT NULL,
    PRIMARY KEY(Pizza_ID),
    FOREIGN KEY(Order_ID) REFERENCES ordertable(Order_ID),
    FOREIGN KEY(Pizza_CrustType, Pizza_Size) REFERENCES baseprice(Pizza_CrustType, Pizza_Size)
);

CREATE TABLE pizza_discount
(
	Pizza_ID int,
    Discount_ID int,
    PRIMARY KEY(Pizza_ID, Discount_ID),
    FOREIGN KEY(Pizza_ID) REFERENCES pizza(Pizza_ID),
    FOREIGN KEY(Discount_ID) REFERENCES discount(Discount_ID)
);

CREATE TABLE topping
(
	Topping_ID int auto_increment,
    Topping_Name varchar(255) NOT NULL,
    Topping_PriceToCustomer float NOT NULL,
    Topping_PriceToBusiness float NOT NULL,
    Topping_QuantityForPersonal float NOT NULL,
    Topping_QuantityForMedium float NOT NULL,
    Topping_QuantityForLarge float NOT NULL,
    Topping_QuantityForXLarge float NOT NULL,
    Topping_CurInvLevel float NOT NULL,
    Topping_MinInvLevel float,
    PRIMARY KEY(Topping_ID)
);

CREATE TABLE pizza_topping
(
	Pizza_ID int,
    Topping_ID int,
    Is_Double boolean,
    PRIMARY KEY(Pizza_ID, Topping_ID),
    FOREIGN KEY(Pizza_ID) REFERENCES pizza(Pizza_ID),
    FOREIGN KEY(Topping_ID) REFERENCES topping(Topping_ID) 
);