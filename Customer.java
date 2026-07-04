package ecommereceOrderManagement;

public class Customer {
 int id;
 String name;
 String contact;

 public Customer(int id, String name, String contact) {
     this.id = id;
     this.name = name;
     this.contact = contact;
 }

 @Override
 public String toString() {
     return String.format("ID:%d | %s | %s", id, name, contact);
 }
}
