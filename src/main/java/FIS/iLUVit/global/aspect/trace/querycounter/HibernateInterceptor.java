package FIS.iLUVit.global.aspect.trace.querycounter;

import lombok.RequiredArgsConstructor;
import org.hibernate.EmptyInterceptor;
import org.springframework.stereotype.Component;

import static FIS.iLUVit.global.aspect.trace.TraceSupports.countQuery;

/**
 * 쿼리 수 측정을 위한 hibernateInterceptor 커스터 마이징
 */
@RequiredArgsConstructor
@Component
public class HibernateInterceptor extends EmptyInterceptor {

    /**
     *  JPA 의 onPrepareStatement 직전에 수행하게 되는 메서드
     */
    @Override
    public String onPrepareStatement(String sql) {
        countQuery();
        return sql;
    }
}
