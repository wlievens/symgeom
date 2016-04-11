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
        value = value.simplify();

        Value left = getLeft().simplify();
        Value right = getRight().simplify();

        if (value.isZero().isTrue())
        {
            Sign leftSign = left.getSign();
            Sign rightSign = right.getSign();
            if (leftSign.isZero() || rightSign.isZero())
            {
                return Tribool.FALSE;
            }
            if (leftSign.isKnown() && rightSign.isKnown())
            {
                return Tribool.of(leftSign.isPositive() != rightSign.isPositive());
            }
        }

        if (value.isInteger())
        {
            if (left.isInteger() || left.isFraction())
            {
                // A*B < C -> B < C/A
                Value a = left;
                Value b = right;
                Value c = value;
                return b.lt(c.divide(a).simplify());
            }
            if (right.isInteger() || right.isFraction())
            {
                return left.simplify().lt(value.divide(right).simplify());
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
