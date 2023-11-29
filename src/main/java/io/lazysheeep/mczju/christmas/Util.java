package io.lazysheeep.mczju.christmas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
}
