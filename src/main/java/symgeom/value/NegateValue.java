package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class NegateValue extends AbstractUnaryValue
{
    private NegateValue(Value operand)
    {
        super(operand);
    }

    public Value simplify()
    {
        Value operand = getOperand().simplify();
        if (operand instanceof NegateValue)
        {
            return ((NegateValue)operand).getOperand();
        }
        if (operand.isInteger())
        {
            int number = operand.asInteger();
            if (number != Integer.MIN_VALUE)
            {
                return Value.number(-number);
            }
        }
        if (operand instanceof AbstractBinaryValue)
        {
            Value left = ((AbstractBinaryValue)operand).getLeft();
            Value right = ((AbstractBinaryValue)operand).getRight();
            if (operand instanceof DivideValue)
            {
                if (left.isInteger())
                {
                    return left.negate().simplify().divide(right);
                }
                if (right.isInteger())
                {
                    return left.simplify().divide(right.negate());
                }
                return left.negate().divide(right);
            }
            if (operand instanceof AddValue)
            {
                left = left.negate().simplify();
                right = right.negate().simplify();
                Value s1 = add(left, right);
                Value s2 = s1.simplify();
                return s2;
            }
            if (operand instanceof MultiplyValue)
            {
                left = left.negate().simplify();
                right = right.negate().simplify();
                return left.negate().multiply(right);
            }
        }

        return create(operand);
    }

    @Override
    public Tribool lt(Value value)
    {
        value = value.simplify();
        if (value.isZero().isTrue())
        {
            // -A < 0  ->  TRUE if A>0
            if (getOperand().isStrictlyPositive().isTrue())
            {
                return Tribool.TRUE;
            }
        }
        return super.lt(value);
    }

    @Override
    public UnaryOperator getOperator()
    {
        return UnaryOperator.NEGATE;
    }

    public static Value create(Value operand)
    {
        return new NegateValue(operand);
    }
}
