package symgeom.comparator;

import symgeom.value.Value;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public interface ComparatorRule
{
    String getLabel();

    boolean match(Value left, Value right);

    Order compare(Value left, Value right);

    public static ComparatorRule create(String label, BiPredicate<Value, Value> match, BiFunction<Value, Value, Order> compare)
    {
        return new ComparatorRule()
        {
            @Override
            public String getLabel()
            {
                return label;
            }

            @Override
            public boolean match(Value left, Value right)
            {
                return match.test(left, right);
            }

            @Override
            public Order compare(Value left, Value right)
            {
                return compare.apply(left, right);
            }
        };
    }
}
