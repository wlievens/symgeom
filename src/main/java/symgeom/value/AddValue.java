package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class AddValue extends AbstractBinaryValue
{
    private AddValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value simplify()
    {
        Value left = getLeft().simplify();
        Value right = getRight().simplify();

        if (left.isInteger() && right.isInteger())
        {
            long result = (long)left.asInteger() + (long)right.asInteger();
            if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE)
            {
                return Value.number((int)result);
            }
        }

        if (left.isInteger() && right instanceof AddValue)
        {
            // A+(B+C) -> (A+B)+C  [if A and B integer]
            Value a = left;
            Value b = ((AddValue)right).getLeft();
            Value c = ((AddValue)right).getRight();
            Value ab = a.add(b).simplify();
            if (ab.isInteger())
            {
                return ab.add(c).simplify();
            }
        }

        if (left instanceof DivideValue || right instanceof DivideValue)
        {
            // A/B + C/D --> (AD-CB)/BD

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

            Value ad = a.multiply(d).simplify();
            Value cb = c.multiply(b).simplify();
            Value bd = b.multiply(d).simplify();

            return ad.add(cb).divide(bd).simplify();
        }

        if (left.isZero().isTrue())
        {
            return right;
        }

        if (right.isZero().isTrue())
        {
            return left;
        }

        if (right instanceof NegateValue)
        {
            return left.subtract(((NegateValue)right).getOperand());
        }

        if (right instanceof SubtractValue)
        {
            // A+(B-C) -> (A-C)+B  [if simpler]
            Value a = left;
            Value b = ((SubtractValue)right).getLeft();
            Value c = ((SubtractValue)right).getRight();
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
        value = value.simplify();
        if (value.isInteger())
        {
            if (getLeft().isInteger())
            {
                return getRight().lt(value.subtract(getLeft()).simplify());
            }
            if (getRight().isInteger())
            {
                return getLeft().lt(value.subtract(getRight()).simplify());
            }
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
