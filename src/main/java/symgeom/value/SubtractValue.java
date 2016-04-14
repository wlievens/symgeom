package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class SubtractValue extends AbstractBinaryValue
{
    private SubtractValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value old_simplify()
    {
        Value left = getLeft().simplify();
        Value right = getRight().simplify();

        if (left.eq(right).isTrue())
        {
            return Value.ZERO;
        }

        if (left.isInteger() && right.isInteger())
        {
            long result = (long)left.asInteger() - (long)right.asInteger();
            if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE)
            {
                return Value.number((int)result);
            }
        }

        if (left instanceof DivideValue || right instanceof DivideValue)
        {
            // A/B - C/D --> (AD-CB)/BD

            Value a;
            Value b;
            if (left instanceof DivideValue)
            {
                DivideValue divide = (DivideValue)left;
                a = divide.getLeft();
                b = divide.getRight();
            }
            else
            {
                a = left;
                b = ONE;
            }

            Value c;
            Value d;
            if (right instanceof DivideValue)
            {
                DivideValue divide = (DivideValue)right;
                c = divide.getLeft();
                d = divide.getRight();
            }
            else
            {
                c = right;
                d = ONE;
            }

            Value ad = a.multiply(d);
            Value cb = c.multiply(b);
            Value bd = b.multiply(d);

            return ad.subtract(cb).divide(bd).simplify();
        }

        if (left instanceof AddValue)
        {
            // (A+B)-C --> (A-C)+B  [if simplier]
            Value a = ((AddValue)left).getLeft();
            Value b = ((AddValue)left).getRight();
            Value c = right;
            if (a.isInteger() && c.isInteger())
            {
                return a.subtract(c).simplify().add(b).simplify();
            }
        }

        return create(left, right);
    }

    @Override
    public Tribool lt(Value value)
    {
        System.out.println(this + " < " + value);
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value.simplify();
        if (b.isInteger())
        {
            // A-B < C  ->  A < C+B
            return a.lt(c.add(b).simplify());
        }
        return super.lt(value);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.SUBTRACT;
    }

    public static Value create(Value left, Value right)
    {
        return new SubtractValue(left, right);
    }
}
