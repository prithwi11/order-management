package com.example.ordermanagement.order.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.exception.CustomerNotFoundException;
import com.example.ordermanagement.customer.repository.CustomerRepository;
import com.example.ordermanagement.inventory.service.InventoryService;
import com.example.ordermanagement.order.dto.request.CreateOrderRequest;
import com.example.ordermanagement.order.dto.request.CreateOrderRequest.CreateOrderItemRequest;
import com.example.ordermanagement.order.entity.Order;
import com.example.ordermanagement.order.entity.OrderStatus;
import com.example.ordermanagement.order.exception.OrderNotFoundException;
import com.example.ordermanagement.order.repository.OrderRepository;
import com.example.ordermanagement.orderitem.entity.OrderItem;
import com.example.ordermanagement.product.entity.Product;
import com.example.ordermanagement.product.exception.ProductNotFoundException;
import com.example.ordermanagement.product.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;

@Service 
public class OrderService {
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private InventoryService inventoryService;

    public OrderService(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository, InventoryService inventoryService) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
    }

    private static final DateTimeFormatter REF_DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    @Transactional 
    public Order createOrder(CreateOrderRequest request) {
        // 1. Validate the customer exists
        Customer customer = customerRepository.findById(request.customerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        // 2. Consolidate duplicate product ids in the request
        Map<Long, Integer> consolidatedQuantities = consolidatedItems(request.items());

        // 3. Retrieve all requested products in a single query
        Map<Long, Product> productsById = productRepository.findAllById(consolidatedQuantities.keySet())
            .stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (Long productId: consolidatedQuantities.keySet()) {
            Product product = productsById.get(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product not found with id: " + productId);
            }
            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new IllegalStateException("Product is inactive and cannot be ordered: " + product.getSku());
            }
        }
        inventoryService.reserveStockBulk(consolidatedQuantities);

        // 4. Initialize order with reference and status PENDING
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderReference(this.generateOrderReference());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 5. Build Order Items
        for(Map.Entry<Long, Integer> entry: consolidatedQuantities.entrySet()) {
            Product product = productsById.get(entry.getKey());
            OrderItem item = new OrderItem(product, entry.getValue());
            totalAmount = totalAmount.add(item.getSubtotal());
            order.addItem(item);
        }
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    private Map<Long, Integer> consolidatedItems(List<CreateOrderItemRequest> items) {
        Map<Long, Integer> consolidated = new LinkedHashMap<>();
        for (CreateOrderItemRequest item: items) {
            consolidated.merge(item.productId(), item.quantity(), Math::addExact);
        }
        return consolidated;
    }

    private String generateOrderReference() {
        String datePart = REF_DATE_FORMAT.format(Instant.now());
        String randomPart = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }

    @Transactional 
    public Order confirmOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        order.confirm();
        inventoryService.consumeStockBulk(extractProductQuantities(order));
        return order;
    }
    @Transactional 
    public Order startProcessingOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        order.startProcessing();
        return order;
    }

    @Transactional 
    public Order shipOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        order.ship();
        return order;
    }

    @Transactional
    public Order deliverOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        order.deliver(); // Validates SHIPPED -> DELIVERED
        return order;
    }

    @Transactional 
    public Order cancelOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        OrderStatus previousStatus = order.getStatus();

        order.cancel();

        if (previousStatus == OrderStatus.PENDING) {
            inventoryService.releaseStockBulk(extractProductQuantities(order));
        }
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return getOrderOrThrow(orderId);
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    private Map<Long, Integer> extractProductQuantities(Order order) {
        Map<Long, Integer> quantities = new TreeMap<>();
        for (OrderItem item : order.getItems()) {
            quantities.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
        }
        return quantities;
    }
}
