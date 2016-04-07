package symgeom.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public final class MultiplyValue extends AbstractBinaryValue
{
    private MultiplyValue(Value left, Value right)
    {
        super(left, right);
    }

    public Value simplify()
    {
        Value left = getLeft().simplify();
        Value right = getRight().simplify();
        if (left.isInteger() && right.isInteger())
        {
            long result = (long)left.asInteger() * (long)right.asInteger();
            if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE)
            {
                return Value.number((int)result);
            }
        }

        if (left instanceof DivideValue || right instanceof DivideValue)
        {
            // A/B * C/D --> AC/BD

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

            Value ac = a.multiply(c);
            Value bd = b.multiply(d);

            System.out.println("DIV " + ac + "] / [" + bd);
            return ac.divide(bd);
        }

        if (right instanceof MultiplyValue)
        {
            // A*(B*C) -> (A*B)*C  (if simpler)
            Value a = left;
            Value b = ((MultiplyValue)right).getLeft();
            Value c = ((MultiplyValue)right).getRight();
            Value abc = a.multiply(b).multiply(c).simplify();
            if (abc.getDepth() < getDepth())
            {
                return abc;
            }
        }

        if (right instanceof DivideValue)
        {
            // A*(B/C) -> (A/B)*C  (if simpler)
            Value a = left;
            Value b = ((DivideValue)right).getLeft();
            Value c = ((DivideValue)right).getRight();
            Value abc = a.divide(b).multiply(c);
            if (abc.getDepth() < getDepth())
            {
                return abc;
            }
        }

        return create(left, right);
    }

    @Override
    public Tribool lt(Value value)
    {
        if (value.isZero().isTrue())
        {
            Sign left = getLeft().getSign();
            Sign right = getRight().getSign();
            if (left.isZero() || right.isZero())
            {
                return Tribool.FALSE;
            }
            if (left.isKnown() && right.isKnown())
            {
                return Tribool.of(left.isPositive() != right.isPositive());
            }
        }
        return super.lt(value);
    }

    @Override
    public BinaryOperator getOperator()
    {
        return BinaryOperator.MULTIPLY;
    }

    public static Value create(Value left, Value right)
    {
        return new MultiplyValue(left, right);
    }
}
