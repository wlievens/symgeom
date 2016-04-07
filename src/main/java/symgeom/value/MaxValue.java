package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class MaxValue extends AbstractBinaryValue
{
    private MaxValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value simplify()
    {
        Value left = getLeft().simplify();
        Value right = getRight().simplify();
        boolean leftInt = left.isInteger();
        boolean rightInt = right.isInteger();
        return create(left, right);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.MAX;
    }

    public static Value create(Value left, Value right)
    {
        return new MaxValue(left, right);
    }
}
