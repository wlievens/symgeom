package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class PowerValue extends AbstractBinaryValue
{
    private PowerValue(Value left, Value right)
    {
        super(left, right);
    }

    @Override
    public String toExpression(int precedence)
    {
        if (getRight().equals(HALF))
        {
            return "sqrt(" + getLeft().toString() + ")";
        }
        return super.toExpression(precedence);
    }

    public Tribool old_lt(Value value)
    {
        // A^B < C
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value.simplify();
        Sign cSign = c.getSign();
        if (cSign.isZero())
        {
            if (b.equals(HALF))
            {
                // SQRT(A) < 0  ->  [false if A > 0]
                return a.gt(ZERO).invert();
            }
        }
        if (cSign.isNegative())
        {
            if (b.equals(HALF))
            {
                // SQRT is never negative
                return Tribool.FALSE;
            }
        }
        if (b instanceof DivideValue)
        {
            // A^(D/E) < C
            DivideValue divide = (DivideValue)b;
            Value d = divide.getLeft();
            Value e = divide.getRight();
            if (cSign.isPositive() && d.eq(Value.ONE).isTrue())
            {
                // A^(1/E) < C  ->  A < C^E
                Value ce = c.power(e).simplify();
                return a.lt(ce);
            }
        }
        return super.lt(c);
    }

    @Override
    public Tribool eqInternal(Value value)
    {
        if (!simplify().isInteger())
        {
            // If we can't simplify to an integer, any comparison with an integer will be false
            return Tribool.FALSE;
        }
        return this.multiply(this).simplify().eq(value.simplify());
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.POWER;
    }

    public static Value create(Value left, Value right)
    {
        return new PowerValue(left, right);
    }
}
