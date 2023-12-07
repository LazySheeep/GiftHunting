package io.lazysheeep.mczju.christmas;

import org.bukkit.util.Vector;

import java.util.*;

public class CUtil
{
    private static final Random random = new Random();

    public static <T> List<T> randomPick(List<T> list, int amount)
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

    public static <T> T randomPickOne(List<T> list)
    {
        return list.get(Math.abs(random.nextInt()) % list.size());
    }

    public static float getRandomFloat(float min, float max)
    {
        return min + random.nextFloat() * (max-min);
    }

    public static Vector getRandomOffset(float intensityX, float intensityY, float intensityZ)
    {
        Vector vec = new Vector();
        if(intensityX != 0.0f) vec.setX((random.nextFloat()-0.5f) * 2.0f * intensityX);
        if(intensityY != 0.0f) vec.setY((random.nextFloat()-0.5f) * 2.0f * intensityY);
        if(intensityZ != 0.0f) vec.setZ((random.nextFloat()-0.5f) * 2.0f * intensityZ);
        return vec;
    }

    public static boolean getRandomBool(float probability)
    {
        return (random.nextFloat() < probability);
    }

    public static <T> List<T> castList(List<?> list, Class<T> clazz)
    {
        if(list == null) return null;
        List<T> result = new ArrayList<>();
        for (Object item : list)
        {
            result.add(clazz.cast(item));
        }
        return result;
    }

    public static <K, V> List<Map<K, V>> castMapList(List<Map<?, ?>> mapList, Class<K> clazzK, Class<V> clazzV)
    {
        if(mapList == null) return null;
        List<Map<K, V>> result = new ArrayList<>();
        for(Map<?, ?> map : mapList)
        {
            Map<K, V> newMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                newMap.put(clazzK.cast(entry.getKey()), clazzV.cast(entry.getValue()));
            }
            result.add(newMap);
        }
        return result;
    }
}
