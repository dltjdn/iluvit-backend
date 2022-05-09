package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CenterAndDistance {
    private Center center;
    private double dist;
}
