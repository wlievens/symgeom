package symgeom.comparator;

import symgeom.value.AddValue;
import symgeom.value.DivideValue;
import symgeom.value.MultiplyValue;
import symgeom.value.PowerValue;
import symgeom.value.Value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static symgeom.comparator.Order.UNKNOWN;

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
                "equals",
                (left, right) -> left.equals(right),
                (left, right) -> Order.EQUAL
        ));
        rules.add(ComparatorRule.create(
                "integer vs integer",
                (left, right) -> left.isInteger() && right.isInteger(),
                (left, right) -> Order.of(left.asInteger().compareTo(right.asInteger()))
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
                "A*B vs C  ->  B vs C/A  if A and C numeric and A positive",
                (left, right) -> left instanceof MultiplyValue && right.isNumeric() && ((MultiplyValue)left).getLeft().isNumeric() && ((MultiplyValue)left).getLeft().isPositive().isTrue(),
                (left, right) -> compare(((MultiplyValue)left).getRight(), right.divide(((MultiplyValue)left).getLeft()))
        ));
        rules.add(ComparatorRule.create(
                "A*B vs C  ->  C/A vs B  if A and C numeric and A negative",
                (left, right) -> left instanceof MultiplyValue && right.isNumeric() && ((MultiplyValue)left).getLeft().isNumeric() && ((MultiplyValue)left).getLeft().isNegative().isTrue(),
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
    }

    public Order compare(Value left, Value right)
    {
        left = left.simplify();
        right = right.simplify();
        System.out.printf("compare [%s] vs [%s]%n", left, right);
        for (ComparatorRule rule : rules)
        {
            if (rule.match(left, right))
            {
                System.out.println("    match " + rule.getLabel());
                Order order = rule.compare(left, right);
                if (order != null && order != UNKNOWN)
                {
                    System.out.println("<-- " + order);
                    return order;
                }
            }
            if (rule.match(right, left))
            {
                System.out.println("    match " + rule.getLabel());
                Order order = rule.compare(right, left);
                if (order != null && order != UNKNOWN)
                {
                    System.out.println("<-- " + order);
                    return order.invert();
                }
            }
        }
        return UNKNOWN;
    }
}
