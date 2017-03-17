package test.selenium.metrics.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Resource {

    private String url;
    private String type;

    @Setter(AccessLevel.NONE)
    private List<Long> times = new ArrayList<>();

    public void setType(String type) {
        if (type != null && type.indexOf(";") != -1) {
            this.type = type.substring(0, type.indexOf(";"));
        } else {
            this.type = type;
        }
    }

    public long getAverageTime() {
        long total = 0L;
        if (times.size() < 1) {
            return total;
        } else if (times.size() < 4) {
            for (Long t : times) {
                total += t;
            }
            return total / times.size();
        } else {
            long min = Long.MAX_VALUE;
            long max = 0;
            for (Long t : times) {
                if (t > max) {
                    max = t;
                }
                if (t < min) {
                    min = t;
                }
                total += t;
            }
            return (total - max - min) / (times.size() - 2);
        }
    }

}
