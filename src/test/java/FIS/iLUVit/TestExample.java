package FIS.iLUVit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;


@ExtendWith(MockitoExtension.class)
public class TestExample {

    private static String TALISKER = "Talisker";
    private static String HIGHLAND_PARK = "Highland Park";
    private Warehouse warehouse = new WarehouseImpl();
    @Mock
    private Warehouse mockWarehouse;

    @BeforeEach
    protected void setUp() throws Exception {
        warehouse.add(TALISKER, 50);
        warehouse.add(HIGHLAND_PARK, 25);
    }

    @Test
    public void testOrderIsFilledIfEnoughInWarehouse() {
        Order order = new Order(TALISKER, 50);
        // exercise
        order.fill(warehouse);
        // verify 상태검증
        Assertions.assertThat(order.isFilled()).isTrue();
        Assertions.assertThat(warehouse.getInventory(TALISKER)).isEqualTo(0);
    }

    @Test
    public void testOrderDoesNotRemoveIfNotEnough() {
        Order order = new Order(TALISKER, 51);
        // exercise
        order.fill(warehouse);
        // verify 상태 검증
        Assertions.assertThat(order.isFilled()).isFalse();
        Assertions.assertThat(warehouse.getInventory(TALISKER)).isEqualTo(50);
    }

    @Test
    public void mockOrderTest() throws Exception {
        //given
        // mocking 혹은 stubbing
        Order order = new Order(TALISKER, 50);
        Order order2 = new Order(TALISKER, 51);

        Mockito.doReturn(true).when(mockWarehouse).getProduct(TALISKER, 50);
        Mockito.doReturn(false).when(mockWarehouse).getProduct(TALISKER, 51);
        //when

        order.fill(mockWarehouse);
        order2.fill(mockWarehouse);
        //then

        Mockito.verify(mockWarehouse, Mockito.times(1)).getProduct(TALISKER, 50);
        Mockito.verify(mockWarehouse, Mockito.times(1)).getProduct(TALISKER, 51);

        Assertions.assertThat(order.isFilled()).isTrue();
        Assertions.assertThat(order2.isFilled()).isFalse();
    }

    private class Warehouse {

        public HashMap<String, Integer> inventory = new HashMap<>();

        public void add(String productName, Integer cnt) {
            inventory.put(productName, cnt);
        }

        public Integer getInventory(String productName) {
            return inventory.get(productName);
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
