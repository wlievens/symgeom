package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class MinValue extends AbstractBinaryValue
{
    private MinValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value old_simplify()
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
        return BinaryOperator.MIN;
    }

    public static Value create(Value left, Value right)
    {
        return new MinValue(left, right);
    }
}
