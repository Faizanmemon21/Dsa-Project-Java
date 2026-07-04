package ecommereceOrderManagement;

public class ProductDLLNode {
 Product product;
 ProductDLLNode prev, next;

 public ProductDLLNode(Product p) {
     this.product = p;
     this.prev = null;
     this.next = null;
 }
}
