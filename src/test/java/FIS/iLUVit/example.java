package FIS.iLUVit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class example {
    private static String TALISKER = "Talisker";
    private static String HIGHLAND_PARK = "Highland Park";
    private Warehouse warehouse = new WarehouseImpl();

    @BeforeEach
    protected void setUp() throws Exception {
        warehouse.add(TALISKER, 50);
        warehouse.add(HIGHLAND_PARK, 25);
    }

    @Test
    public void testOrderIsFilledIfEnoughInWarehouse() {
        Order order = new Order(TALISKER, 50);
        order.fill(warehouse);
        assertTrue(order.isFilled());
        assertEquals(0, warehouse.getInventory(TALISKER));
    }

    @Test
    public void testOrderDoesNotRemoveIfNotEnough() {
        Order order = new Order(TALISKER, 51);
        order.fill(warehouse);
        assertFalse(order.isFilled());
        assertEquals(50, warehouse.getInventory(TALISKER));
    }

    private class Warehouse {

        public HashMap<String, Integer> inventory = new HashMap<>();

        public void add(String productName, Integer cnt) {
            inventory.put(productName, cnt);
        }

        public Optional<Integer> getInventory(String productName) {
            return Optional.ofNullable(inventory.get(productName));
        }

        public boolean getProduct(String productName, Integer size) {
            Integer sizeOfProduct = inventory.get(productName);
            if(sizeOfProduct < size)
                return false;
            else {
                inventory.put(productName, sizeOfProduct - size);
                return true;
            }
        }
    }

    private class WarehouseImpl extends Warehouse {
    }

    private class Order {
        public String productName;
        public Integer size;
        public Boolean filled = false;

        public Order(String productName, Integer size) {
            this.productName = productName;
            this.size = size;
        }

        public void fill(Warehouse warehouse) {
            filled = warehouse.getProduct(productName, size);
        }

        public Boolean isFilled() {
            return filled;
        }
    }
}
