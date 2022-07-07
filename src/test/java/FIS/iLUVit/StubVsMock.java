package FIS.iLUVit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StubVsMock {

    private static String TALISKER = "Talisker";
    private static String HIGHLAND_PARK = "Highland Park";
    private Warehouse stubWarehouse = new WarehouseImpl();
    private MailServiceStub stubMailService = new MailServiceStub();

    @Mock
    private MailService mockMailService;
    @Mock
    private Warehouse mockWarehouse;

    public interface MailService {
        public void send (String msg);
    }

    public class MailServiceStub implements MailService {
        private List<String> messages = new ArrayList<String>();

        public void send (String msg) {
            messages.add(msg);
        }
        public int numberSent() {
            return messages.size();
        }
    }

    @BeforeEach
    void setup(){
        stubWarehouse.add(TALISKER, 50);
        stubWarehouse.add(HIGHLAND_PARK, 25);
    }


    @Test
    public void 이메일_stub_test() throws Exception {
        //given
        Order order = new Order(TALISKER, 50);
        order.setMailService(stubMailService);

        // exercise
        order.fill(stubWarehouse);
        // verify 상태검증
        Assertions.assertThat(order.isFilled()).isTrue();
        Assertions.assertThat(stubMailService.numberSent()).isEqualTo(1);
        Assertions.assertThat(stubWarehouse.getInventory(TALISKER)).isEqualTo(0);
        //then
    }

    @Test
    public void 이메일_mock_test() throws Exception {
        //given
        // mocking 혹은 stubbing
        Order order = new Order(TALISKER, 50);
        order.setMailService(mockMailService);

        Mockito.doNothing().when(mockMailService).send(ArgumentMatchers.any(String.class));
        Mockito.doReturn(true).when(mockWarehouse).getProduct(TALISKER, 50);
        //when
        order.fill(mockWarehouse);
        //then
        Mockito.verify(mockWarehouse, Mockito.times(1)).getProduct(TALISKER, 50);
        Mockito.verify(mockMailService, Mockito.times(1)).send(ArgumentMatchers.any(String.class));
        Assertions.assertThat(order.isFilled()).isTrue();
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
        public MailService mailService;

        public Order(String productName, Integer size) {
            this.productName = productName;
            this.size = size;
        }

        public void setMailService(MailService mailService) {
            this.mailService = mailService;
        }

        public void fill(Warehouse warehouse) {
            if (warehouse.getProduct(productName, size)) {
                filled = true;
                mailService.send("test");
            }
        }

        public Boolean isFilled() {
            return filled;
        }
    }
}
