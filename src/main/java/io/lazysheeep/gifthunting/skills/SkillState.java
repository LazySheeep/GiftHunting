package io.lazysheeep.gifthunting.skills;

public class SkillState
{
    public int charges;
    public int cooldownTimer;
    public int aftercastTimer;
    public boolean overlayAftercastApplied;
    public boolean overlayCooldownApplied;
    public boolean enabled;

    public SkillState(int initialCharges)
    {
        this.charges = initialCharges;
        this.cooldownTimer = 0;
        this.aftercastTimer = 0;
        this.overlayAftercastApplied = false;
        this.overlayCooldownApplied = false;
        this.enabled = false;
    }
}
