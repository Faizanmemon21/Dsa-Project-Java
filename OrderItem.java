package ecommereceOrderManagement;

public class OrderItem {
 Product product;
 int qty;
 OrderItem next;

 public OrderItem(Product p, int qty) {
     this.product = p;
     this.qty = qty;
     this.next = null;
 }
}
