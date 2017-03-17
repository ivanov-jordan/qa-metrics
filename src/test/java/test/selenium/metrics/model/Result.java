package test.selenium.metrics.model;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Result {

    public String url;
    public int cycles;
    public Collection<Resource> results;

}
