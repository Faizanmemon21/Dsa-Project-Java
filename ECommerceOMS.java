package ecommereceOrderManagement;

import java.util.Scanner;

public class ECommerceOMS {

 static final int MAX_PRODUCTS = 100;
 static final int MAX_CUSTOMERS = 100;

 static Product[] products = new Product[MAX_PRODUCTS]; 
 static int productCount = 0;

 static Customer[] customers = new Customer[MAX_CUSTOMERS];
 static int customerCount = 0;

 static OrderNode ordersHead = null; 
 static int nextOrderId = 1001;

 static ProductDLLNode productDLLHead = null; 
 static ProductDLLNode productDLLTail = null;

 static Scanner sc = new Scanner(System.in);

 // ---------- SAMPLE DATA ----------
 static void loadSampleData() {
     addProduct(new Product(1, "Basic T-Shirt", "Apparel", 1200.0, 50));
     addProduct(new Product(2, "Headphones", "Electronics", 4500.0, 20));
     addProduct(new Product(3, "Coffee Mug", "Home", 800.0, 30));
     addProduct(new Product(4, "Novel - The Wanderer", "Books", 700.0, 15));
     addProduct(new Product(5, "Gaming Mouse", "Electronics", 3500.0, 12));

     addCustomer(new Customer(101, "Aisha Khan", "0312-000000"));
     addCustomer(new Customer(102, "Omar Ali", "0301-111111"));
 }

 // ---------- PRODUCTS ----------
 static void addProduct(Product p) {
     if (productCount >= MAX_PRODUCTS) {
         System.out.println("Product storage full.");
         return;
     }
     products[productCount++] = p;

     // Add to doubly linked list tail for browsing
     ProductDLLNode node = new ProductDLLNode(p);
     if (productDLLHead == null) {
         productDLLHead = productDLLTail = node;
     } else {
         productDLLTail.next = node;
         node.prev = productDLLTail;
         productDLLTail = node;
     }
 }

 static Product findProductById(int id) {
     for (int i = 0; i < productCount; i++)
         if (products[i].id == id) return products[i];
     return null;
 }

 // ---------- CUSTOMERS ----------
 static void addCustomer(Customer c) {
     if (customerCount >= MAX_CUSTOMERS) {
         System.out.println("Customer storage full.");
         return;
     }
     customers[customerCount++] = c;
 }

 static Customer findCustomerById(int id) {
     for (int i = 0; i < customerCount; i++)
         if (customers[i].id == id) return customers[i];
     return null;
 }

 // ---------- ORDERS ----------
 static void placeOrder() {
     System.out.println("Enter Customer ID:");
     int cid = readInt();
     Customer c = findCustomerById(cid);
     if (c == null) {
         System.out.println("Customer not found. Add new customer? (y/n)");
         if (sc.next().toLowerCase().charAt(0) == 'y') {
             sc.nextLine();
             System.out.print("Name: ");
             String name = sc.nextLine();
             System.out.print("Contact: ");
             String contact = sc.nextLine();
             c = new Customer(cid, name, contact);
             addCustomer(c);
         } else return;
     }

     OrderNode order = new OrderNode(nextOrderId++, c);
     System.out.println("Placing order for customer: " + c.name);
     boolean adding = true;
     sc.nextLine(); // flush
     while (adding) {
         System.out.println("Enter product ID to add (or 0 to finish):");
         int pid = readInt();
         if (pid == 0) break;
         Product p = findProductById(pid);
         if (p == null) { System.out.println("Invalid product ID."); continue; }
         System.out.printf("Enter qty (available %d): ", p.stock);
         int qty = readInt();
         if (qty <= 0 || qty > p.stock) { System.out.println("Invalid qty."); continue; }

         OrderItem item = new OrderItem(p, qty);
         item.next = order.itemsHead; order.itemsHead = item;

         order.total += p.price * qty;
         p.stock -= qty;
         p.popularity += qty;
         System.out.println("Added " + qty + " x " + p.name);

         System.out.println("Add more items? (y/n)");
         String ans = sc.next();
         if (!ans.toLowerCase().startsWith("y")) adding = false;
     }

     if (order.itemsHead == null) { System.out.println("No items added. Cancelling order."); return; }

     System.out.println("Do you have a discount code? (enter or 'no')");
     sc.nextLine();
     String code = sc.nextLine().trim();
     double discount = 0.0;
     if (!code.equalsIgnoreCase("no") && !code.isEmpty()) {
         discount = applyDiscount(code, order.total);
         if (discount > 0) {
             System.out.printf("Discount Rs %.2f applied.\n", discount);
             order.total -= discount;
         } else System.out.println("Invalid or expired discount.");
     }

     order.next = ordersHead; ordersHead = order; // append to singly linked list

     addDeliveryEvent(order, "Pending");
     System.out.printf("Order placed. Order ID: %d | Total: Rs %.2f\n", order.orderId, order.total);
 }

 static double applyDiscount(String code, double total) {
     if (code.equalsIgnoreCase("SAVE10")) return total * 0.10;
     if (code.equalsIgnoreCase("FLAT500") && total > 2000) return 500.0;
     return 0.0;
 }

 static void addDeliveryEvent(OrderNode order, String status) {
     String ts = java.time.LocalDateTime.now().toString();
     DeliveryEvent e = new DeliveryEvent(status, ts);
     if (order.deliveryHead == null) order.deliveryHead = e;
     else {
         DeliveryEvent cur = order.deliveryHead;
         while (cur.next != null) cur = cur.next;
         cur.next = e;
     }
 }

 static OrderNode findOrderById(int oid) {
     OrderNode cur = ordersHead;
     while (cur != null) {
         if (cur.orderId == oid) return cur;
         cur = cur.next;
     }
     return null;
 }

 static void updateDeliveryStatus() {
     System.out.println("Enter Order ID to update:");
     int oid = readInt();
     OrderNode o = findOrderById(oid);
     if (o == null) { System.out.println("Order not found."); return; }
     System.out.println("Current delivery history:");
     printDeliveryHistory(o);
     System.out.println("Enter new status (Packed / Out For Delivery / Delivered / Returned):");
     sc.nextLine();
     String s = sc.nextLine();
     addDeliveryEvent(o, s);
     System.out.println("Status updated.");
 }

 static void printDeliveryHistory(OrderNode o) {
     DeliveryEvent e = o.deliveryHead;
     while (e != null) {
         System.out.println(e.timestamp + " -> " + e.status);
         e = e.next;
     }
 }

 static void processReturn() {
     System.out.println("Enter Order ID for return:");
     int oid = readInt();
     OrderNode o = findOrderById(oid);
     if (o == null) { System.out.println("Order not found."); return; }
     if (o.refunded) { System.out.println("This order has already been refunded."); return; }
     System.out.println("Marking order as Returned and refunding stock.");
     OrderItem it = o.itemsHead;
     while (it != null) {
         it.product.stock += it.qty;
         it.product.popularity -= it.qty;
         it = it.next;
     }
     o.refunded = true;
     addDeliveryEvent(o, "Returned");
     System.out.printf("Order %d marked as returned. Refund processed.\n", o.orderId);
 }

 // ---------- SEARCH ----------
 static void searchProductsByName() {
     System.out.println("Enter name to search (partial allowed):");
     sc.nextLine();
     String q = sc.nextLine().toLowerCase();
     boolean found = false;
     for (int i = 0; i < productCount; i++) {
         if (products[i].name.toLowerCase().contains(q)) {
             System.out.println(products[i]);
             found = true;
         }
     }
     if (!found) System.out.println("No products matched.");
 }

 static void searchProductsByCategory() {
     System.out.println("Enter category:");
     sc.nextLine();
     String q = sc.nextLine().toLowerCase();
     boolean found = false;
     for (int i = 0; i < productCount; i++) {
         if (products[i].category.toLowerCase().equals(q)) {
             System.out.println(products[i]);
             found = true;
         }
     }
     if (!found) System.out.println("No products in that category.");
 }

 static void searchOrderById() {
     System.out.println("Enter Order ID to search:");
     int oid = readInt();
     OrderNode o = findOrderById(oid);
     if (o == null) System.out.println("Order not found.");
     else {
         System.out.println("Order ID: " + o.orderId);
         System.out.println("Customer: " + o.customer);
         System.out.println("Items:");
         OrderItem it = o.itemsHead;
         while (it != null) {
             System.out.printf("  %s x %d = Rs %.2f\n", it.product.name, it.qty, it.product.price*it.qty);
             it = it.next;
         }
         System.out.printf("Total: Rs %.2f | Refunded: %b\n", o.total, o.refunded);
         System.out.println("Delivery history:");
         printDeliveryHistory(o);
     }
 }

 // ---------- SORTING: Bubble Sort ----------
 static void sortProductsByPrice() {
     for (int i = 0; i < productCount - 1; i++) {
         for (int j = 0; j < productCount - i - 1; j++) {
             if (products[j].price > products[j + 1].price) {
                 Product temp = products[j];
                 products[j] = products[j + 1];
                 products[j + 1] = temp;
             }
         }
     }
     rebuildProductDLL();
     System.out.println("Products sorted by price (low -> high).");
 }

 static void sortProductsByName() {
     for (int i = 0; i < productCount - 1; i++) {
         for (int j = 0; j < productCount - i - 1; j++) {
             if (products[j].name.compareToIgnoreCase(products[j + 1].name) > 0) {
                 Product temp = products[j];
                 products[j] = products[j + 1];
                 products[j + 1] = temp;
             }
         }
     }
     rebuildProductDLL();
     System.out.println("Products sorted by name (A->Z).");
 }

 static void sortProductsByPopularity() {
     for (int i = 0; i < productCount - 1; i++) {
         for (int j = 0; j < productCount - i - 1; j++) {
             if (products[j].popularity < products[j + 1].popularity) {
                 Product temp = products[j];
                 products[j] = products[j + 1];
                 products[j + 1] = temp;
             }
         }
     }
     rebuildProductDLL();
     System.out.println("Products sorted by popularity (high -> low).");
 }

 static void rebuildProductDLL() {
     productDLLHead = productDLLTail = null;
     for (int i = 0; i < productCount; i++) {
         ProductDLLNode node = new ProductDLLNode(products[i]);
         if (productDLLHead == null) productDLLHead = productDLLTail = node;
         else {
             productDLLTail.next = node;
             node.prev = productDLLTail;
             productDLLTail = node;
         }
     }
 }

 // ---------- RECURSION ----------
 static int countOrdersRecursive(OrderNode head) {
     if (head == null) return 0;
     return 1 + countOrdersRecursive(head.next);
 }

 static double totalRevenueRecursive(OrderNode head) {
     if (head == null) return 0.0;
     return (head.refunded ? 0.0 : head.total) + totalRevenueRecursive(head.next);
 }

 // ---------- CUSTOMER SUMMARY ----------
 static void customerSummary() {
     System.out.println("Enter Customer ID:");
     int cid = readInt();
     Customer c = findCustomerById(cid);
     if (c == null) { System.out.println("Customer not found."); return; }
     System.out.println("Customer: " + c);
     int totalOrders = 0; double totalSpent = 0;
     java.util.Map<String, Integer> catCount = new java.util.HashMap<>();
     OrderNode cur = ordersHead;
     while (cur != null) {
         if (cur.customer.id == c.id && !cur.refunded) {
             totalOrders++;
             totalSpent += cur.total;
             OrderItem it = cur.itemsHead;
             while (it != null) {
                 catCount.put(it.product.category, catCount.getOrDefault(it.product.category,0)+it.qty);
                 it = it.next;
             }
         }
         cur = cur.next;
     }
     System.out.println("Total Orders: " + totalOrders);
     System.out.printf("Total Spent: Rs %.2f\n", totalSpent);
     if (catCount.isEmpty()) System.out.println("No purchases yet.");
     else {
         System.out.println("Category counts:");
         for (String cat : catCount.keySet()) System.out.println("  " + cat + " -> " + catCount.get(cat));
     }
 }

 // ---------- PRODUCT BROWSING (DLL) ----------
 static void browseProductsDLL() {
     if (productDLLHead == null) { System.out.println("No products available."); return; }
     ProductDLLNode cur = productDLLHead;
     while (true) {
         System.out.println("\nCurrent Product:");
         System.out.println(cur.product);
         System.out.println("[P]revious  [N]ext  [E]xit browsing");
         String cmd = sc.next().toLowerCase();
         if (cmd.startsWith("p")) {
             if (cur.prev != null) cur = cur.prev; else System.out.println("Already at first product.");
         } else if (cmd.startsWith("n")) {
             if (cur.next != null) cur = cur.next; else System.out.println("Already at last product.");
         } else break;
     }
 }

 // ---------- HELPERS ----------
 static int readInt() {
     while (true) {
         try { return sc.nextInt(); } 
         catch (Exception ex) { sc.nextLine(); System.out.println("Invalid input. Enter a number:"); }
     }
 }

 static void printAllProducts() { for (int i=0;i<productCount;i++) System.out.println(products[i]); }
 static void printAllOrders() { 
     OrderNode cur = ordersHead; 
     while(cur!=null){ System.out.println("Order ID:"+cur.orderId+" | Customer:"+cur.customer.name+" | Total: Rs "+cur.total); cur=cur.next;}
 }

 // ---------- MAIN MENU ----------
 public static void main(String[] args) {
     loadSampleData();
     boolean running = true;
     while(running){
         System.out.println("\n--- E-Commerce OMS ---");
         System.out.println("1. Browse Products");
         System.out.println("2. Place Order");
         System.out.println("3. Update Delivery Status");
         System.out.println("4. Process Return");
         System.out.println("5. Search Products By Name");
         System.out.println("6. Search Products By Category");
         System.out.println("7. Search Order By ID");
         System.out.println("8. Sort Products By Price");
         System.out.println("9. Sort Products By Name");
         System.out.println("10. Sort Products By Popularity");
         System.out.println("11. Customer Summary");
         System.out.println("12. Total Orders (Recursive)");
         System.out.println("13. Total Revenue (Recursive)");
         System.out.println("0. Exit");
         int choice = readInt();
         switch(choice){
             case 1: browseProductsDLL(); break;
             case 2: placeOrder(); break;
             case 3: updateDeliveryStatus(); break;
             case 4: processReturn(); break;
             case 5: searchProductsByName(); break;
             case 6: searchProductsByCategory(); break;
             case 7: searchOrderById(); break;
             case 8: sortProductsByPrice(); break;
             case 9: sortProductsByName(); break;
             case 10: sortProductsByPopularity(); break;
             case 11: customerSummary(); break;
             case 12: System.out.println("Total Orders: "+countOrdersRecursive(ordersHead)); break;
             case 13: System.out.printf("Total Revenue: Rs %.2f\n",totalRevenueRecursive(ordersHead)); break;
             case 0: running=false; break;
             default: System.out.println("Invalid choice."); break;
         }
     }
     System.out.println("Exiting OMS.");
 }
}

