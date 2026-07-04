package ecommereceOrderManagement;

public class Product {
 int id;
 String name;
 
 String category;
 double price;
 int stock;
 int popularity; 

 public Product(int id, String name, String cat, double price, int stock) {
     this.id = id;
     this.name = name;
     this.category = cat;
     this.price = price;
     this.stock = stock;
     this.popularity = 0;
 }

 @Override
 public String toString() {
     return String.format("ID:%d | %s | %s | Rs %.2f | Stock:%d | Popularity:%d",
             id, name, category, price, stock, popularity);
 }
}