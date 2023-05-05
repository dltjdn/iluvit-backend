package FIS.iLUVit.repository.iluvit;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPresentation.presentation;

public class PresentationQueryMethod {

    public static List<OrderSpecifier> presentationSort(Pageable pageable) {

        List<OrderSpecifier> orders = new ArrayList<>();

        if (pageable.getSort().isEmpty()) {
            // 기본값 == 날짜 오름차순 정리
            orders.add(new OrderSpecifier<>(Order.ASC, presentation.endDate));
            orders.add(new OrderSpecifier<>(Order.ASC, presentation.id));
        }
        else {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "id":
                        OrderSpecifier<?> orderId = QueryDslUtil.getSortedColumn(direction, presentation, "id");
                        orders.add(orderId);
                        break;
                    case "score":
                        OrderSpecifier<?> orderScore = QueryDslUtil.getSortedColumn(direction, center, "score");
                        orders.add(orderScore);
                        break;
                    case "name":
                        OrderSpecifier<?> orderName = QueryDslUtil.getSortedColumn(direction, center, "name");
                        orders.add(orderName);
                        break;
                    default:
                        break;
                }

            }
        }
        return orders;
    }
}
