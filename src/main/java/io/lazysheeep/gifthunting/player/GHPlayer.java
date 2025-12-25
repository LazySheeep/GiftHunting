package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.buffs.Buff;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.skills.Skill;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.skills.SkillState;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import io.lazysheeep.gifthunting.factory.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.buffs.DawnBuff;

public class GHPlayer
{
    private Player _hostPlayer;
    private GameInstance _gameInstance;
    private int _score = 0;
    private final HashSet<Buff> _buffs = new HashSet<>();
    private final Map<Skill, SkillState> _skillStates = new HashMap<>();
    public int lastClickGiftTime = 0;

    public @NotNull Player getPlayer()
    {
        return _hostPlayer;
    }

    public String getName()
    {
        return _hostPlayer.getName();
    }

    public UUID getUUID()
    {
        return _hostPlayer.getUniqueId();
    }

    public Location getLocation()
    {
        return _hostPlayer.getLocation();
    }

    public Location getBodyLocation()
    {
        return _hostPlayer.getLocation().add(_hostPlayer.getEyeLocation()).multiply(0.5f);
    }

    public Location getEyeLocation()
    {
        return _hostPlayer.getEyeLocation();
    }

    public GameInstance getGameInstance()
    {
        return _gameInstance;
    }

    public void destroy()
    {
        _hostPlayer = null;
    }

    public boolean isConnected()
    {
        return _hostPlayer != null && _hostPlayer.isConnected();
    }

    public boolean isDestroyed()
    {
        return _hostPlayer == null;
    }

    public boolean isValid()
    {
        return isConnected() && !isDestroyed();
    }

    public int getScore()
    {
        return _score;
    }

    public void addScore(int score)
    {
        LazuliUI.sendMessage(_hostPlayer, MessageFactory.getActionbarSuffixWhenScoreChanged(_score, score));
        this._score += score;
    }

    public void setScore(int score)
    {
        this._score = score;
    }

    GHPlayer() {}

    public void onConnect(@NotNull GameInstance gameInstance, @NotNull Player player)
    {
        _gameInstance = gameInstance;
        _hostPlayer = player;
        if(gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING)
        {
            enableSkill(Skill.BOOST);
            enableSkill(Skill.COUNTER);
            enableSkill(Skill.DETECT);
        }
        updateSlot();
    }

    public void onDisconnect(@NotNull GameInstance gameInstance)
    {
        disableAllSkills();
        updateSlot();
    }

    public void onDestroy(@NotNull GameInstance gameInstance)
    {
        reset();
    }

    public void addBuff(@NotNull Buff buff)
    {
        for(Buff existingBuff : _buffs)
        {
            if(existingBuff.tryMerge(buff))
            {
                return;
            }
        }
        _buffs.add(buff);
        buff.onApply(this);
    }

    public boolean hasBuff(@NotNull Class<? extends Buff> buffClass)
    {
        for(Buff buff : _buffs)
        {
            if(buff.getClass() == buffClass)
            {
                return true;
            }
        }
        return false;
    }

    public List<Buff> getBuffs()
    {
        return new ArrayList<>(_buffs);
    }

    public void removeBuff(@NotNull Class<? extends Buff> buffClass)
    {
        for(Buff buff : _buffs)
        {
            if(buff.getClass() == buffClass)
            {
                buff.onRemove(this);
                _buffs.remove(buff);
                return;
            }
        }
    }

    public void clearBuffs()
    {
        for(Buff buff : _buffs)
        {
            buff.onRemove(this);
        }
        _buffs.clear();
    }

    public void enableAllSkills()
    {
        for(Skill skill : Skill.values())
        {
            enableSkill(skill);
        }
    }

    public void enableSkill(Skill skill)
    {
        SkillState skillState = getSkillState(skill);
        if(!skillState.enabled)
        {
            skill.onEnable(this, skillState);
            skillState.enabled = true;
        }
    }

    public void disableAllSkills()
    {
        for(Skill skill : Skill.values())
        {
            disableSkill(skill);
        }
    }

    public void disableAndResetAllSkills()
    {
        for(Skill skill : Skill.values())
        {
            disableSkill(skill);
        }
        _skillStates.clear();
    }

    public void disableSkill(Skill skill)
    {
        SkillState skillState = getSkillState(skill);
        if(skillState.enabled)
        {
            skill.onDisable(this, skillState);
            skillState.enabled = false;
        }
    }

    public boolean useSkill(@NotNull Skill skill)
    {
        SkillState skillState = getSkillState(skill);
        return skill.tryUse(this, skillState);
    }

    private SkillState getSkillState(Skill skill)
    {
        SkillState st = _skillStates.get(skill);
        if(st == null)
        {
            st = skill.createDefaultState();
            _skillStates.put(skill, st);
        }
        return st;
    }

    public void reset()
    {
        _score = 0;
        lastClickGiftTime = 0;
        clearBuffs();
        disableAndResetAllSkills();
        _hostPlayer.clearActivePotionEffects();
        _hostPlayer.setGlowing(false);
    }

    private void autoCraftBigClub()
    {
        PlayerInventory inv = _hostPlayer.getInventory();
        int totalClub = 0;
        for(ItemStack it : inv)
        {
            if(CustomItem.checkItem(it) == CustomItem.STICK)
            {
                totalClub += it.getAmount();
            }
        }
        if(totalClub >= 10)
        {
            MCUtil.RemoveItem(_hostPlayer, CustomItem.STICK, 10);
            MCUtil.GiveItem(_hostPlayer, CustomItem.SUPER_STICK.create());
        }
    }

    public void updateSlot()
    {
        PlayerInventory inv = _hostPlayer.getInventory();
        HashMap<Integer, CustomItem> lockedMap = new HashMap<>();
        for(CustomItem ci : CustomItem.values())
        {
            if(ci.lockedSlot >= 0 && ci.lockedSlot < inv.getSize())
            {
                lockedMap.put(ci.lockedSlot, ci);
            }
        }

        ArrayList<ItemStack> itemCache = new ArrayList<>();
        // remove wrong items from locked slots
        for(Map.Entry<Integer, CustomItem> e : lockedMap.entrySet())
        {
            int slot = e.getKey();
            CustomItem target = e.getValue();
            ItemStack slotItem = inv.getItem(slot);
            CustomItem slotType = CustomItem.checkItem(slotItem);
            if(slotItem != null && slotType != CustomItem.PLACEHOLDER && slotType != target)
            {
                inv.setItem(slot, null);
                itemCache.add(slotItem);
            }
        }
        // collect locked items from non-locked slots
        HashSet<Integer> lockedSlots = new HashSet<>(lockedMap.keySet());
        HashMap<CustomItem, Integer> totalCounts = new HashMap<>();
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(lockedSlots.contains(i)) continue;
            ItemStack it = inv.getItem(i);
            CustomItem type = CustomItem.checkItem(it);
            if(type != null && type.lockedSlot >= 0)
            {
                totalCounts.put(type, totalCounts.getOrDefault(type, 0) + it.getAmount());
                inv.setItem(i, null);
            }
        }
        // place locked items
        for(Map.Entry<Integer, CustomItem> e : lockedMap.entrySet())
        {
            int slot = e.getKey();
            CustomItem target = e.getValue();
            int available = totalCounts.getOrDefault(target, 0);
            if(available <= 0) continue;

            ItemStack current = inv.getItem(slot);
            CustomItem currentType = CustomItem.checkItem(current);
            if(current != null && currentType == target)
            {
                int max = current.getMaxStackSize();
                int space = Math.max(0, max - current.getAmount());
                if(space > 0)
                {
                    int use = Math.min(space, available);
                    current.setAmount(current.getAmount() + use);
                    totalCounts.put(target, available - use);
                }
            }
            else
            {
                ItemStack stack = target.create();
                int max = stack.getMaxStackSize();
                int toPlace = Math.min(max, available);
                stack.setAmount(toPlace);
                inv.setItem(slot, stack);
                totalCounts.put(target, available - toPlace);
            }
        }
        // place placeholders
        for(Map.Entry<Integer, CustomItem> e : lockedMap.entrySet())
        {
            int slot = e.getKey();
            ItemStack slotItem = inv.getItem(slot);
            if(slotItem == null)
            {
                inv.setItem(slot, CustomItem.PLACEHOLDER.create());
            }
        }
        // restore cached items
        for(ItemStack cached : itemCache)
        {
            inv.addItem(cached);
        }
    }

    void tick()
    {
        if(_hostPlayer == null || !_hostPlayer.isConnected())
        {
            return;
        }

        _hostPlayer.setSaturation(20.0f);
        _hostPlayer.setFoodLevel(20);

        autoCraftBigClub();

        // manage Dawn buff
        boolean shouldHave = _score >= (int)(_gameInstance.getVictoryScore() * 0.8f);
        boolean has = hasBuff(DawnBuff.class);
        if(shouldHave && !has)
        {
            addBuff(new DawnBuff(-1));
        }
        else if(!shouldHave && has)
        {
            removeBuff(DawnBuff.class);
        }
        // tick buffs
        for(Buff buff : _buffs.stream().toList())
        {
            buff.tick(this);
            if(buff.getRemainingTime() == 0)
            {
                buff.onRemove(this);
                _buffs.remove(buff);
            }
        }
        // show buffs actionbar only if not holding a skill item
        ItemStack inHand = _hostPlayer.getInventory().getItemInMainHand();
        CustomItem held = CustomItem.checkItem(inHand);
        boolean holdingSkill = false;
        if(held != null)
        {
            for(Skill s : Skill.values())
            {
                if(s.skillItem == held)
                {
                    holdingSkill = true;
                    break;
                }
            }
        }
        if(!holdingSkill)
        {
            LazuliUI.sendMessage(_hostPlayer, MessageFactory.getBuffsActionbar(new java.util.ArrayList<>(_buffs)));
        }
        // manage Dawn skill
        boolean shouldEnable = _gameInstance.anyPlayerHasDawnBuff();
        boolean isEnabled = getSkillState(Skill.DAWN).enabled;
        if(shouldEnable && !isEnabled)
        {
            enableSkill(Skill.DAWN);
        }
        else if(!shouldEnable && isEnabled)
        {
            disableSkill(Skill.DAWN);
        }
        // tick skills
        for(Map.Entry<Skill, SkillState> e : _skillStates.entrySet())
        {
            Skill skill = e.getKey();
            SkillState skillState = e.getValue();
            if(skillState.enabled)
            {
                skill.tick(this, skillState);
            }
        }
        // update scoreboard
        GiftHunting.GetPlugin().getScoreObjective().getScore(_hostPlayer).setScore(_score);
    }
}
