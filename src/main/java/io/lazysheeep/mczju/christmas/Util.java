package io.lazysheeep.mczju.christmas;

import java.util.*;

public class Util
{
    public static <T> List<T> RandomPick(List<T> list, int amount)
    {
        List<T> ret = new ArrayList<T>();
        int[] array = new int[list.size()];

        if(amount > array.length) amount = array.length;
        for(int i = 0; i < array.length; i ++)
        {
            array[i] = i;
        }

        Random random = new Random();
        for(int i = 0; i < amount; i ++)
        {
            int r = Math.abs(random.nextInt()) % (array.length - i);
            ret.add(list.get(array[r]));
            array[r] = array[array.length - i - 1];
        }

        return ret;
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
