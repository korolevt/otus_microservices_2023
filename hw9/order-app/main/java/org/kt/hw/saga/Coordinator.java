package org.kt.hw.saga;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class Coordinator {
    private Saga saga;
    private boolean ended;

    public Coordinator(Saga s) {
        this.saga = s;
    }

    public void commit() throws Exception {
        if (this.ended) {
            return;
        }

        for (int i = 0; i < this.saga.getSteps().size(); i++) {
            try {
                this.execStep(i);
            } catch (Exception e) {
                throw e;
            }
        }

        this.ended = true;
    }

    private void execStep(int i) throws Exception {
        Saga.Function f = saga.getSteps().get(i).getFunc();
        if (f == null) {
            throw new Exception("func must be function");
        }

        try {
            Method m = f.getClass().getMethod("op");
            m.setAccessible(true);
            Object[] args = {};
            m.invoke(f, args);
        } catch (Exception e) {
            abort(i);
            throw e;
        }
    }

    // Прерываем все, что было до этого шага
    private void abort(int from) {
        for (int i = from; i >= 0; i--) {
            try {
                this.abortStep(i);
            } catch (Exception e) {
                // ignore errors during abort
            }
        }
    }

    private void abortStep(int step) throws Exception {
        Saga.Function f = this.saga.getSteps().get(step).getCompensation();
        if (f == null) {
            throw new Exception("compensation must be function");
        }

        Method m = f.getClass().getMethod("op");
        m.setAccessible(true);
        Object[] args = {};
        m.invoke(f, args);
    }
}