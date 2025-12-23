package io.lazysheeep.gifthunting.skills;

public class SkillState
{
    public int charges;
    public int cooldownTimer;
    public int aftercastTimer;
    public boolean enabled;
    public boolean overlayAftercastApplied;
    public boolean overlayCooldownApplied;

    public SkillState()
    {
        this.charges = 0;
        this.cooldownTimer = -1;
        this.aftercastTimer = -1;
        this.enabled = false;
        this.overlayAftercastApplied = false;
        this.overlayCooldownApplied = false;
    }
}
