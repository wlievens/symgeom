package symgeom.linear;

import com.google.common.collect.ImmutableList;
import symgeom.util.Util;
import symgeom.value.AbstractBinaryValue;
import symgeom.value.AddValue;
import symgeom.value.DivideValue;
import symgeom.value.MultiplyValue;
import symgeom.value.Value;

public class LinearExpressionBuilder
{
    public LinearExpression build(Value value)
    {
        return new LinearExpression(buildTerms(value));
    }

    private ImmutableList<LinearTerm> buildTerms(Value value)
    {
        if (value instanceof AddValue)
        {
            return ImmutableList.<LinearTerm>builder()
                    .addAll(buildTerms(((AddValue)value).getLeft()))
                    .addAll(buildTerms(((AddValue)value).getRight()))
                    .build();
        }
        return ImmutableList.of(buildTerm(value));
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
                            int gcd = Util.gcd(a, b);
                            a /= gcd;
                            b /= gcd;
                            if (b < 0)
                            {
                                a *= -1;
                                b *= -1;
                            }
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
        return LinearTerm.create(1, 1, value);
    }
}
