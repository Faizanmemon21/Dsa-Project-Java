package ecommereceOrderManagement;


public class OrderNode {
 int orderId;
 Customer customer;
 OrderItem itemsHead; 
 double total;
 boolean refunded;
 DeliveryEvent deliveryHead; 
 OrderNode next;

 public OrderNode(int oid, Customer c) {
     this.orderId = oid;
     this.customer = c;
     this.itemsHead = null;
     this.total = 0.0;
     this.refunded = false;
     this.deliveryHead = null;
     this.next = null;
 }
}
