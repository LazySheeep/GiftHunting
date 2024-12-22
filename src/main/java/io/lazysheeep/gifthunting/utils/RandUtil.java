package io.lazysheeep.gifthunting.utils;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandUtil
{
    private static final Random random = new Random(System.currentTimeMillis());

    public static <T> List<T> Pick(List<T> list, int amount)
    {
        List<T> ret = new ArrayList<>();
        int[] array = new int[list.size()];

        if(amount > array.length) amount = array.length;
        for(int i = 0; i < array.length; i ++)
        {
            array[i] = i;
        }

        for(int i = 0; i < amount; i ++)
        {
            int r = Math.abs(random.nextInt()) % (array.length - i);
            ret.add(list.get(array[r]));
            array[r] = array[array.length - i - 1];
        }

        return ret;
    }

    public static <T> T Pick(List<T> list)
    {
        return list.get(nextInt(0, list.size() - 1));
    }

    public static float nextFloat(float min, float max)
    {
        return random.nextFloat(min, max);
    }

    public static int nextInt(int min, int max)
    {
        return random.nextInt(min, max + 1);
    }

    public static Vector nextVector(float intensityX, float intensityY, float intensityZ)
    {
        Vector vec = new Vector();
        vec.setX((random.nextFloat()-0.5f) * 2.0f * intensityX);
        vec.setY((random.nextFloat()-0.5f) * 2.0f * intensityY);
        vec.setZ((random.nextFloat()-0.5f) * 2.0f * intensityZ);
        return vec;
    }

    public static boolean nextBool(float probability)
    {
        return (random.nextFloat() < probability);
    }
}
