package symgeom.comparator;

import symgeom.util.Util;
import symgeom.value.AddValue;
import symgeom.value.DivideValue;
import symgeom.value.MultiplyValue;
import symgeom.value.PowerValue;
import symgeom.value.Value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static symgeom.comparator.Order.GREATER;
import static symgeom.comparator.Order.UNKNOWN;
import static symgeom.value.Value.ZERO;

public class Comparator
{
    private final List<ComparatorRule> rules = new ArrayList<>();

    public Comparator()
    {
        setupRules();
    }

    private void setupRules()
    {
        rules.add(ComparatorRule.create(
                "integer vs integer",
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> Order.of(left.asInteger().compareTo(right.asInteger()))
        ));
        rules.add(ComparatorRule.create(
                "equals",
                (left, right) -> left.equals(right),
                (left, right) -> Order.EQUAL
        ));
        rules.add(ComparatorRule.create(
                "pi vs integer",
                (left, right) -> left == Value.PI && right.isInteger(),
                (left, right) -> Order.of(right.asInteger().compareTo(BigInteger.valueOf(3)) <= 0 ? +1 : -1)
        ));
        rules.add(ComparatorRule.create(
                "A+B vs C  ->  B vs C-A  if A and C numeric",
                (left, right) -> left instanceof AddValue && ((AddValue)left).getLeft().isNumeric() && right.isNumeric(),
                (left, right) -> compare(((AddValue)left).getRight(), right.subtract(((AddValue)left).getLeft()))
        ));
        rules.add(ComparatorRule.create(
                "A+B vs C  ->  A vs C-B  if B and C numeric",
                (left, right) -> left instanceof AddValue && ((AddValue)left).getRight().isNumeric() && right.isNumeric(),
                (left, right) -> compare(((AddValue)left).getLeft(), right.subtract(((AddValue)left).getRight()))
        ));
        rules.add(ComparatorRule.create(
                "A/B vs C/D  ->  A vs BC/D  if B, C, D positive",
                (left, right) -> left instanceof DivideValue && ((DivideValue)left).getRight().isInteger() && right.isFraction(),
                (left, right) -> compare(((DivideValue)left).getLeft(), ((DivideValue)left).getRight().multiply(right))
        ));
        rules.add(ComparatorRule.create(
                "A*B vs 0  ->  A vs 0  if B positive",
                (left, right) -> right.equals(ZERO) && left instanceof MultiplyValue && isPositive(((MultiplyValue)left).getRight()),
                (left, right) -> compare(((MultiplyValue)left).getLeft(), ZERO)
        ));
        rules.add(ComparatorRule.create(
                "A*B vs 0  ->  B vs 0  if A positive",
                (left, right) -> right.equals(ZERO) && left instanceof MultiplyValue && isPositive(((MultiplyValue)left).getLeft()),
                (left, right) -> compare(((MultiplyValue)left).getRight(), ZERO)
        ));
        rules.add(ComparatorRule.create(
                "A*B vs C  ->  B vs C/A  if A divides C and A positive",
                (left, right) -> left instanceof MultiplyValue
                        && right.isInteger()
                        && ((MultiplyValue)left).getLeft().isInteger()
                        && right.asInteger().mod(((MultiplyValue)left).getLeft().asInteger()).equals(BigInteger.ZERO)
                        && isPositive(((MultiplyValue)left).getLeft()),
                (left, right) -> compare(((MultiplyValue)left).getRight(), right.divide(((MultiplyValue)left).getLeft()))
        ));
        rules.add(ComparatorRule.create(
                "A*B vs C  ->  C/A vs B  if A and C numeric and A negative",
                (left, right) -> left instanceof MultiplyValue && right.isNumeric() && ((MultiplyValue)left).getLeft().isNumeric() && isNegative(((MultiplyValue)left).getLeft()),
                (left, right) -> compare(right.divide(((MultiplyValue)left).getLeft()), ((MultiplyValue)left).getRight())
        ));
        rules.add(ComparatorRule.create(
                "A+B vs C+D  ->  A-C vs D-B   if A and C numeric",
                (left, right) -> left instanceof AddValue && right instanceof AddValue && ((AddValue)left).getLeft().isNumeric() && ((AddValue)right).getLeft().isNumeric(),
                (left, right) -> compare(((AddValue)left).getLeft().subtract(((AddValue)right).getLeft()), ((AddValue)right).getRight().subtract(((AddValue)left).getRight()))
        ));
        rules.add(ComparatorRule.create(
                "A/B vs C/D  ->  AD vs BC",
                (left, right) -> left instanceof DivideValue || right instanceof DivideValue,
                (left, right) -> {
                    Value a;
                    Value b;
                    Value c;
                    Value d;
                    if (left instanceof DivideValue)
                    {
                        a = ((DivideValue)left).getLeft();
                        b = ((DivideValue)left).getRight();
                    }
                    else
                    {
                        a = left;
                        b = Value.ONE;
                    }
                    if (right instanceof DivideValue)
                    {
                        c = ((DivideValue)right).getLeft();
                        d = ((DivideValue)right).getRight();
                    }
                    else
                    {
                        c = right;
                        d = Value.ONE;
                    }
                    Value ad = a.multiply(d).simplify();
                    Value bc = b.multiply(c).simplify();
                    return compare(ad, bc);
                }
        ));
        rules.add(ComparatorRule.create(
                "A^(B/C) vs D  ->  A^B vs sign(D)*D^C",
                (left, right) -> left instanceof PowerValue && ((PowerValue)left).getRight() instanceof DivideValue,
                (left, right) -> {
                    Value a = ((PowerValue)left).getLeft();
                    Value b = ((DivideValue)((PowerValue)left).getRight()).getLeft();
                    Value c = ((DivideValue)((PowerValue)left).getRight()).getRight();
                    Value d = right;
                    return compare(a.power(b), d.sign().multiply(d.power(c)));
                }
        ));
        rules.add(ComparatorRule.create(
                "A vs B  ->  GREATER  if A positive, B negative, B != 0",
                (left, right) -> !right.equals(ZERO) && isPositive(left) && isNegative(right),
                (left, right) -> GREATER
        ));
    }

    private boolean isNegative(Value value)
    {
        return compare(value, ZERO) == Order.LESSER;
    }

    private boolean isPositive(Value value)
    {
        return compare(value, ZERO) == Order.GREATER;
    }

    private int level = -1;

    public Order compare(Value left, Value right)
    {
        // shortcut for integer comparison
        if (left.isInteger() && right.isInteger())
        {
            return Order.of(left.asInteger().compareTo(right.asInteger()));
        }

        ++level;
        left = left.simplify();
        right = right.simplify();
        String indent = Util.repeat("    ", level);
        System.out.printf(indent + "compare [%s] vs [%s]%n", left, right);
        for (ComparatorRule rule : rules)
        {
            System.out.printf("%s--> check '%s'%n", indent, rule.getLabel());
            if (rule.match(left, right))
            {
                System.out.printf("%s    match %d%n", indent, level);
                Order order = rule.compare(left, right);
                if (order != null && order != UNKNOWN)
                {
                    System.out.println(indent + "<-- " + order);
                    --level;
                    return order;
                }
            }
            System.out.printf("%s--> inverse check '%s'%n", indent, rule.getLabel());
            if (rule.match(right, left))
            {
                System.out.printf("%s    match %d%n", indent, level);
                Order order = rule.compare(right, left);
                if (order != null && order != UNKNOWN)
                {
                    System.out.println(indent + "<-- " + order.invert());
                    --level;
                    return order.invert();
                }
            }
        }
        --level;
        System.out.println(indent + "<-- " + UNKNOWN);
        return UNKNOWN;
    }
}
