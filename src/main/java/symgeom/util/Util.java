package symgeom.util;

import com.google.common.collect.ImmutableList;

import java.math.BigInteger;
import java.util.stream.Collector;

public class Util
{
    public static boolean canSubtract(int a, int b)
    {
        return isInt(BigInteger.valueOf(a).subtract(BigInteger.valueOf(b)));
    }

    public static boolean isInt(BigInteger integer)
    {
        return integer.equals(BigInteger.valueOf(integer.intValue()));
    }

    public static boolean canAdd(int a, int b)
    {
        return isInt(BigInteger.valueOf(a).add(BigInteger.valueOf(b)));
    }

    public static boolean canMultiply(int a, int b)
    {
        return isInt(BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)));
    }

    public static boolean canExponentiate(int a, int b)
    {
        return isInt(BigInteger.valueOf(a).pow(b));
    }

    public static int gcd(int a, int b)
    {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).intValue();
    }

    public static int gcd(int first, int... others)
    {
        int result = first;
        for (int other : others)
            result = gcd(result, other);
        return result;
    }

    public static BigInteger lcm(BigInteger a, BigInteger b)
    {
        return a.multiply(b.divide(a.gcd(b)));
    }

    public static BigInteger lcm(BigInteger... input)
    {
        BigInteger result = input[0];
        for (int i = 1; i < input.length; i++)
        {
            result = lcm(result, input[i]);
        }
        return result;
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList()
    {
        return Collector.of(
            ImmutableList::builder,
            ImmutableList.Builder::add,
            (l, r) -> l.addAll(r.build()),
            ImmutableList.Builder<T>::build);
    }
}
