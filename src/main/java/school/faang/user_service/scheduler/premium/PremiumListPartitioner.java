package school.faang.user_service.scheduler.premium;

import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PremiumListPartitioner {
    public List<PremiumAccessBatch> partition(List<Premium> list, int batchSize) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }

        List<List<Premium>> partitions = ListUtils.partition(list, batchSize);

        List<PremiumAccessBatch> batches = new ArrayList<>();
        for (int i = 0; i < partitions.size(); i++) {
            batches.add(new PremiumAccessBatch(partitions.get(i), i + 1));
        }
        return batches;
    }
}
