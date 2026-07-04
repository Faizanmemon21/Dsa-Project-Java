package ecommereceOrderManagement;

public class DeliveryEvent {
 String status; 
 String timestamp; 
 DeliveryEvent next;

 public DeliveryEvent(String s, String t) {
     this.status = s;
     this.timestamp = t;
     this.next = null;
 }
}
