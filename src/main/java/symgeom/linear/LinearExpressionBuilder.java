package symgeom.linear;

import com.google.common.collect.ImmutableList;
import symgeom.value.AbstractBinaryValue;
import symgeom.value.AddValue;
import symgeom.value.DivideValue;
import symgeom.value.IntegerValue;
import symgeom.value.MultiplyValue;
import symgeom.value.Value;

public class LinearExpressionBuilder
{
    public LinearExpression build(Value value)
    {
        return new LinearExpression(buildTerms(value, 1, 1)).simplify();
    }

    private ImmutableList<LinearTerm> buildTerms(Value value, int numerator, int denominator)
    {
        if (value instanceof AbstractBinaryValue)
        {
            Value left = ((AbstractBinaryValue)value).getLeft();
            Value right = ((AbstractBinaryValue)value).getRight();
            if (value instanceof AddValue)
            {
                return ImmutableList.<LinearTerm>builder()
                        .addAll(buildTerms(left, numerator, denominator))
                        .addAll(buildTerms(right, numerator, denominator))
                        .build();
            }
            if (value instanceof DivideValue)
            {
                if (left.isInteger() && right.isInteger())
                {
                    // TODO overflow
                    return ImmutableList.of(LinearTerm.create(numerator * left.asInteger(), denominator * right.asInteger(), Value.ONE));
                }
                if (right.isInteger())
                {
                    return buildTerms(left, numerator, denominator * right.asInteger());
                }
            }
            if (value instanceof MultiplyValue)
            {
                if (left.isInteger())
                {
                    return buildTerms(right, numerator * left.asInteger(), denominator);
                }
            }
        }
        return ImmutableList.of(buildTerm(value).multiply(numerator, denominator));
    }

    private LinearTerm buildTerm(Value value)
    {
        if (value instanceof AbstractBinaryValue)
        {
            Value left = ((AbstractBinaryValue)value).getLeft().simplify();
            Value right = ((AbstractBinaryValue)value).getRight().simplify();
            if (value instanceof DivideValue)
            {
                if (left.isInteger() && right.isInteger())
                {
                    return LinearTerm.create(left.asInteger(), right.asInteger());
                }
                if (left.isInteger())
                {
                    return LinearTerm.create(left.asInteger(), 1, Value.ONE.divide(right));
                }
                if (right.isInteger())
                {
                    if (left instanceof MultiplyValue)
                    {
                        MultiplyValue multiply = (MultiplyValue)left;
                        if (multiply.getLeft().isInteger())
                        {
                            int a = multiply.getLeft().asInteger();
                            int b = right.asInteger();
                            Value c = multiply.getRight();
                            return LinearTerm.create(a, b, c);
                        }
                    }
                    return LinearTerm.create(1, right.asInteger(), left);
                }
            }
            else if (value instanceof MultiplyValue)
            {
                if (left.isInteger() && right.isInteger())
                {
                    // There is some reason (overflow?) why we can't multiply left & right
                    return LinearTerm.create(1, 1, value);
                }
                if (left.isInteger())
                {
                    return LinearTerm.create(left.asInteger(), 1, right);
                }
                if (right.isInteger())
                {
                    return LinearTerm.create(right.asInteger(), 1, left);
                }
                if (left instanceof DivideValue && ((DivideValue)left).getLeft().isInteger() && ((DivideValue)left).getRight().isInteger())
                {
                    return LinearTerm.create(((DivideValue)left).getLeft().asInteger(), ((DivideValue)left).getRight().asInteger(), right);
                }
            }
        }
        if (value instanceof IntegerValue)
        {
            return LinearTerm.create(((IntegerValue)value).getValue(), 1, Value.ONE);
        }
        return LinearTerm.create(1, 1, value);
    }
}
