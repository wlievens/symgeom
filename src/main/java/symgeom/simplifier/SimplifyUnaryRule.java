package symgeom.simplifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import symgeom.value.AbstractUnaryValue;
import symgeom.value.Value;

import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
public final class SimplifyUnaryRule implements SimplifyRule
{
    @Getter
    private final String label;
    private final Predicate<AbstractUnaryValue> match;
    private final Predicate<Value> matchOperand;
    private final Function<Value, Value> execute;

    @Override
    public final Value execute(Value value)
    {
        Value operand = ((AbstractUnaryValue)value).getOperand();
        return execute.apply(operand);
    }

    public final boolean match(Value value)
    {
        if (value instanceof AbstractUnaryValue && match.test((AbstractUnaryValue)value))
        {
            Value operand = ((AbstractUnaryValue)value).getOperand();
            return matchOperand.test(operand);
        }
        return false;
    }

    public static SimplifyUnaryRule create(String label, Predicate<AbstractUnaryValue> match, Predicate<Value> matchOperand, Function<Value, Value> execute)
    {
        return new SimplifyUnaryRule(label, match, matchOperand, execute);
    }
}
