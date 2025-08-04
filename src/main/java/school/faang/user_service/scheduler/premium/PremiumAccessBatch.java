package school.faang.user_service.scheduler.premium;

import school.faang.user_service.entity.premium.Premium;

import java.util.ArrayList;
import java.util.List;

public class PremiumAccessBatch {
    private final List<Premium> premiums;
    private final int batchNumber;

    public PremiumAccessBatch(List<Premium> premiums, int batchNumber) {
        this.premiums = new ArrayList<>(premiums);
        this.batchNumber = batchNumber;
    }

    public List<Premium> getPremiums() {
        return new ArrayList<>(premiums);
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public int getSize() {
        return premiums.size();
    }
}