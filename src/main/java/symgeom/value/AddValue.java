package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class AddValue extends AbstractBinaryValue
{
    private AddValue(Value left, Value right)
    {
        super(left, right);
    }

    public Tribool old_lt(Value value)
    {
        value = value.simplify();
        if (value.isInteger())
        {
            // A+B < C
            Value a = getLeft().simplify();
            Value b = getRight().simplify();
            Value c = value;
            if (a.isInteger())
            {
                // A+B < C  ->  B < C-A
                Value ca = c.subtract(a).simplify();
                return b.lt(ca);
            }
            if (b.isInteger())
            {
                // A+B < C  ->  A < C-B
                return a.lt(c.subtract(b).simplify());
            }
            if (a.isFraction())
            {
                // A+B < C  ->  B < C-A
                Value ca = c.subtract(a).simplify();
                return b.lt(ca);
            }
            if (b.isFraction())
            {
                // A+B < C  ->  A < C-B
                return a.lt(c.subtract(b).simplify());
            }
        }

        if (value.isZero().isTrue())
        {
            // A+B < 0  -> A < -B
            return getLeft().simplify().lt(getRight().negate().simplify());
        }

        return super.lt(value);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.ADD;
    }

    public static Value create(Value left, Value right)
    {
        return new AddValue(left, right);
    }
}
