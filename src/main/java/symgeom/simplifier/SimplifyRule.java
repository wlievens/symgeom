package symgeom.simplifier;

import symgeom.value.Value;

import java.util.function.Function;
import java.util.function.Predicate;

public interface SimplifyRule
{
    String getLabel();

    boolean match(Value value);

    Value execute(Value value);

    public static SimplifyRule create(String label, Predicate<Value> match, Function<Value, Value> execute)
    {
        return new SimplifyRule()
        {
            @Override
            public String getLabel()
            {
                return label;
            }

            @Override
            public boolean match(Value value)
            {
                return match.test(value);
            }

            @Override
            public Value execute(Value value)
            {
                return execute.apply(value);
            }
        };
    }
}
