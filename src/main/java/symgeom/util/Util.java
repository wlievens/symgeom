package symgeom.util;

import com.google.common.collect.ImmutableList;

import java.math.BigInteger;
import java.util.stream.Collector;

public class Util
{
    public static boolean isInt(BigInteger integer)
    {
        return integer.equals(BigInteger.valueOf(integer.intValue()));
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

    public static int lcm(int a, int b)
    {
        // TODO check overflow!
        return a * (b / gcd(a, b));
    }

    public static int lcm(int... input)
    {
        int result = input[0];
        for (int i = 1; i < input.length; i++) result = lcm(result, input[i]);
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
