package org.dummycreator.dummyfactories;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Eyal on 10/27/13.
 */
public class RangeFactory<T extends Number> extends DummyFactory<T> {
    final private T min;
    final private T max;
    final private Generator<T> generator;

    /**
     *
     * @param min inclusive
     * @param max exclusive
     */
    public RangeFactory(T min, T max) {
        super();

        this.min = min;
        this.max = max;
        this.generator = createGenerator(min.getClass());
    }

    @SuppressWarnings("unchecked")
    private Generator<T> createGenerator(Class<? extends  Number> clazz) {
        if(clazz == Integer.class){
            return (Generator<T>) new IntegerGenerator();
        } else if(clazz == Long.class){
            return (Generator<T>) new LongGenerator();
        } else if(clazz == Float.class ){
            return (Generator<T>) new FloatGenerator();
        } else if(clazz == Double.class  ){
            return (Generator<T>) new DoubleGenerator();
        }

        throw new IllegalStateException( String.format("Type %s is not supported by %s", min.getClass(), getClass()));
    }

    @Override
    public T createDummy(Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
        return generator.nextT(min, max);
    }


    @Override
    public boolean isValidForType(Class<? super T> clazz) {
        return clazz.isAssignableFrom(min.getClass());
    }


///////////////////////////////////////////////////////////////////////////////////

    private static interface Generator<T> {
        T nextT(T min, T max);
    }

    private static class IntegerGenerator implements Generator<Integer>{
        @Override
        public Integer nextT(Integer min, Integer max) {
            return ThreadLocalRandom.current().nextInt(min, max);
        }
    }

    private static class LongGenerator implements Generator<Long>{
        @Override
        public Long nextT(Long min, Long max) {
            return ThreadLocalRandom.current().nextLong(min, max);
        }
    }

    private static class FloatGenerator implements Generator<Float>{
        @Override
        public Float nextT(Float min, Float max) {
            return new Float(( ThreadLocalRandom.current().nextDouble(min, max)));
        }
    }

    private static class DoubleGenerator implements Generator<Double>{
        @Override
        public Double nextT(Double min, Double max) {
            return ThreadLocalRandom.current().nextDouble(min, max);
        }
    }
}
