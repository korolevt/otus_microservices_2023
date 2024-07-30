package org.kt.hw.saga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Saga {

    public interface Function {
        public void op();
    }

    @Data
    @AllArgsConstructor
    public static class Step {
        private String name;
        private Function func;
        private Function compensation;
    }

    private String name;
    private List<Step> steps = new ArrayList<>();

    public void addStep(Step step) {
        this.steps.add(step);
    }
}