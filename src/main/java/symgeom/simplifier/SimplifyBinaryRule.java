package symgeom.simplifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.AbstractBinaryValue;
import symgeom.value.Value;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@RequiredArgsConstructor
public final class SimplifyBinaryRule implements SimplifyRule
{
    @Getter
    private final String label;
    private final Predicate<AbstractBinaryValue> match;
    private final BiPredicate<Value, Value> matchOperands;
    private final BiFunction<Value, Value, Value> execute;

    @Override
    public final Value execute(Value value)
    {
        Value left = ((AbstractBinaryValue)value).getLeft();
        Value right = ((AbstractBinaryValue)value).getRight();
        return execute.apply(left, right);
    }

    public final boolean match(Value value)
    {
        if (value instanceof AbstractBinaryValue && match.test((AbstractBinaryValue)value))
        {
            Value left = ((AbstractBinaryValue)value).getLeft();
            Value right = ((AbstractBinaryValue)value).getRight();
            return matchOperands.test(left, right);
        }
        return false;
    }

    public static SimplifyBinaryRule create(String label, Predicate<AbstractBinaryValue> match, BiPredicate<Value, Value> matchOperands, BiFunction<Value, Value, Value> execute)
    {
        return new SimplifyBinaryRule(label, match, matchOperands, execute);
    }
}
