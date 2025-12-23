package io.lazysheeep.gifthunting.skills;

public class SkillState
{
    public boolean enabled;
    public int charges;
    public int cooldownTimer;
    public int activeTimer;
    public boolean overlayAftercastApplied;
    public boolean overlayCooldownApplied;

    public SkillState()
    {
        this.charges = 0;
        this.cooldownTimer = -1;
        this.activeTimer = -1;
        this.enabled = false;
        this.overlayAftercastApplied = false;
        this.overlayCooldownApplied = false;
    }
}
