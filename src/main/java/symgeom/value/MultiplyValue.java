package symgeom.value;

import lombok.EqualsAndHashCode;
import symgeom.linear.LinearExpression;
import symgeom.linear.LinearExpressionBuilder;

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
        if (left.isZero().isTrue())
        {
            return ZERO;
        }

        Value right = getRight().simplify();
        if (right.isZero().isTrue())
        {
            return ZERO;
        }

        if (left.isOne().isTrue())
        {
            return right;
        }
        if (right.isOne().isTrue())
        {
            return left;
        }

        if (left.isInteger() && right.isInteger())
        {
            long result = (long)left.asInteger() * (long)right.asInteger();
            if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE)
            {
                return Value.number((int)result);
            }
        }

        if ((left.isInteger() || left.isFraction()) && right instanceof NegateValue)
        {
            Value a = left.negate().simplify();
            Value b = right.negate().simplify();
            return create(a, b);
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

            Value ac = a.multiply(c).simplify();
            Value bd = b.multiply(d).simplify();

            return ac.divide(bd).simplify();
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

        LinearExpression expression = new LinearExpressionBuilder().build(this);
        return expression.toValue();
    }

    @Override
    public Tribool lt(Value value)
    {
        // A*B < C
        Value a = getLeft().simplify();
        Value b = getRight().simplify();
        Value c = value.simplify();

        if (c.isZero().isTrue())
        {
            Sign leftSign = a.getSign();
            Sign rightSign = b.getSign();
            if (leftSign.isZero() || rightSign.isZero())
            {
                return Tribool.FALSE;
            }
            if (leftSign.isKnown() && rightSign.isKnown())
            {
                return Tribool.of(leftSign.isPositive() != rightSign.isPositive());
            }
        }

        if (c.isInteger())
        {
            if (a.isInteger() || a.isFraction())
            {
                // A*B < C  ->  B <?> C/A
                Sign sign = a.getSign();
                Value ca = c.divide(a).simplify();
                if (sign.isPositive())
                {
                    // A*B < C  ->  B < C/A
                    return b.lt(ca);
                }
                if (sign.isNegative())
                {
                    // A*B < C  ->  B > C/A
                    return b.gt(ca);
                }
            }
            if (b.isInteger() || b.isFraction())
            {
                return a.simplify().lt(c.divide(b).simplify());
            }
        }

        return super.lt(c);
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
