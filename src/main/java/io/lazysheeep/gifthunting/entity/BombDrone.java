package io.lazysheeep.gifthunting.entity;

import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MathUtils;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.*;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class BombDrone extends GHEntity
{
    private final float hoverHeightMin = 8.0f;
    private final float hoverHeightMax = 12.0f;
    private final float hoverRadius = 4.0f;
    private final float chaseDistance = 48.0f;
    private final float attackDistance = 24.0f;
    private final float explodeDistance = 1.0f;
    private final float explodeRange = 5.0f;
    private final float explodeDropScore = 0.075f;
    private final float hoverSpeed = 2.0f;
    private final float maxSpeed = 40.0f;
    private final float attackSpeed = 40.0f;
    private final float CASWeight = 0.8f;
    private final float CASWeightWhenAttacking = 0.2f;
    private final float CASSpeedMin = 20.0f;
    private final float CASSpeedMax = 40.0f;
    private final double radarAngleVertical = 210.0;
    private final double radarAngleHorizontal = 150.0;
    private final double radarScanStepVertical = 30.0;
    private final double radarScanStepHorizontal = 30.0;
    private final int radarScanStepCountVertical = (int) Math.ceil(radarAngleVertical / radarScanStepVertical) + 1;
    ;
    private final int radarScanStepCountHorizontal = (int) Math.ceil(radarAngleHorizontal / radarScanStepHorizontal) + 1;
    private final float radarRangeFrontMultiplier = 6.0f;
    private final float radarRangeSide = 1.0f;
    private final boolean showRadarRayGreenParticle = false;
    private final boolean showRadarRayRedParticle = false;
    private final boolean showPropellerParticle = true;
    private final boolean playPropellerSound = true;

    private final float predScale = 0.5f;
    private final float predMinTimeSec = 0.2f;
    private final float predMaxTimeSec = 1.0f;
    private final float predTimeGrowRate = 1.0f;     // seconds of lead gained per second when similar
    private final float predTimeShrinkRate = 1.2f;   // seconds of lead lost per second when dissimilar
    private final float predDirSimilarDeg = 30.0f;    // direction similarity threshold in degrees [0..180]
    private final float predSpeedSimilarRel = 0.25f;  // relative speed diff threshold (|v-p|/max(v,p)) [0..1]
    private final float predGrowAlignPower = 1.0f;   // growth multiplier power vs alignment
    private final float predShrinkMisalignPower = 1.0f; // shrink multiplier power vs misalignment
    private final float predVelAdjustBase = 1.0f;    // base factor for velocity change speed
    private final float predVelAdjustScale = 2.0f;   // extra factor scaling with difference
    private final float predDiffWeightDir = 0.65f;    // weight of direction difference in [0..1]
    private final float predDiffWeightSpeed = 0.35f;  // weight of speed difference in [0..1]
    private final float predLookAtWeight = 0.4f;

    private static final float heightTolerance = 2.0f;

    private final GHPlayer _targetGHPlayer;
    private final GHPlayer _sourceGHPlayer;
    private Vector _currentVelocity;

    public BombDrone(Location location, GHPlayer sourceGHPlayer, GHPlayer targetGHPlayer)
    {
        super(location);
        this._sourceGHPlayer = sourceGHPlayer;
        this._targetGHPlayer = targetGHPlayer;
        _currentVelocity = new Vector(0, 0, 0);
    }

    private int lastRadarScanStep = 0;
    private final boolean[][] radarFlags = new boolean[radarScanStepCountVertical][radarScanStepCountHorizontal];

    // Prediction state
    private Location lastTargetLocation = null;
    private Vector predictedVelocity = new Vector(0, 1, 0);
    private float predictedTimeSec = 0.0f;

    @Override
    public void onTick(GameInstance gameInstance)
    {
        Location currentLocation = _location.clone();
        Location targetLocation = _targetGHPlayer.getBodyLocation();
        float deltaTime = 0.05f;

        // --- Compute target's current horizontal velocity ---
        Vector currentVel = new Vector(0, 0, 0);
        if (lastTargetLocation != null)
        {
            currentVel = targetLocation.toVector().subtract(lastTargetLocation.toVector()).multiply(1.0 / deltaTime);
        }
        Vector currentHorVel = currentVel.clone()
                                         .setY(0.0); // predict only in horizontal plane; we keep fixed hover height

        // initialize predicted velocity the first time with current
        if (predictedVelocity.lengthSquared() < 1e-8 && currentHorVel.lengthSquared() > 0)
        {
            predictedVelocity = currentHorVel.clone();
        }

        // --- Similarity checks ---
        double curSpeed = currentHorVel.length();
        double predSpeed = predictedVelocity.length();
        double dirCos;
        if (curSpeed > 1e-6 && predSpeed > 1e-6)
        {
            dirCos = currentHorVel.clone().normalize().dot(predictedVelocity.clone().normalize());
        }
        else
        {
            dirCos = -1.0; // treat as dissimilar when one is near zero
        }
        dirCos = Math.max(-1.0, Math.min(1.0, dirCos));

        double relSpeedDiff = 0.0;
        if (Math.max(curSpeed, predSpeed) > 1e-6)
        {
            relSpeedDiff = Math.abs(curSpeed - predSpeed) / Math.max(curSpeed, predSpeed);
        }
        // direction similarity based on angle threshold in degrees
        double angleDeg = Math.toDegrees(Math.acos(dirCos));
        boolean directionSimilar = angleDeg <= predDirSimilarDeg;
        boolean speedSimilar = relSpeedDiff <= predSpeedSimilarRel;
        boolean similar = directionSimilar && speedSimilar;

        // --- Adjust predicted lead time ---
        // alignment in [0,1]: 1 when perfectly aligned, 0 when opposite or undefined
        float align01 = (float) Math.max(0.0, dirCos);
        if (similar)
        {
            float growthFactor = (float) Math.pow(align01, predGrowAlignPower);
            predictedTimeSec += deltaTime * predTimeGrowRate * growthFactor;
        }
        else
        {
            float misalign01 = 1.0f - align01;
            float shrinkFactor = (float) Math.pow(misalign01, predShrinkMisalignPower);
            predictedTimeSec -= deltaTime * predTimeShrinkRate * shrinkFactor;
        }
        predictedTimeSec = Math.clamp(predictedTimeSec, predMinTimeSec, predMaxTimeSec);

        // --- Adjust predicted velocity using weighted average with extra factor ---
        // difference score in [0,1]
        float diffDir = 1.0f - align01; // 0 when aligned, 1 when opposite
        float diffSpeed = Math.clamp((float) relSpeedDiff, 0.0f, 1.0f);
        float diffScore = Math.clamp(predDiffWeightDir * diffDir + predDiffWeightSpeed * diffSpeed, 0.0f, 1.0f);
        // Scale the contribution of current velocity by a factor that grows with difference
        double effectiveDt = deltaTime * (predVelAdjustBase + predVelAdjustScale * diffScore);
        double denom = predictedTimeSec + effectiveDt;
        if (denom > 1e-6)
        {
            Vector num = predictedVelocity.clone()
                                          .multiply(predictedTimeSec)
                                          .add(currentHorVel.clone().multiply(effectiveDt));
            predictedVelocity = num.multiply(1.0 / denom);
        }
        // keep horizontal
        predictedVelocity.setY(0.0);

        // --- Predicted hover target ---
        Vector predictedOffset = predictedVelocity.clone().multiply(predictedTimeSec * predScale);
        Location predictedTargetLocation = targetLocation.clone().add(predictedOffset);
        Vector chh = predictedTargetLocation.toVector().subtract(currentLocation.toVector()).setY(0.0);
        float chhLength = (float) chh.length();
        Location predictedHoverLocation = predictedTargetLocation.clone()
                                                                 .add(0.0, MathUtils.Lerp(hoverHeightMin, hoverHeightMax, (chhLength - hoverRadius) / chaseDistance), 0.0);

        // calculate attack velocity
        Vector currentToTarget = predictedTargetLocation.toVector().subtract(currentLocation.toVector());
        boolean attackFlag = false;
        if (currentToTarget.length() <= attackDistance)
        {
            RayTraceResult result = MathUtils.RayTrace(currentLocation, currentToTarget);
            if (result == null)
            {
                attackFlag = true;
            }
        }
        Vector attackVelocity = currentToTarget.normalize().multiply(attackSpeed);

        // calculate main propeller force direction (to predicted hover location)
        Vector chhNorm = chh.clone().normalize();
        Vector vhNorm = _currentVelocity.clone().setY(0.0).normalize();
        Vector upDirection = new Vector(0.0, 1.0, 0.0);
        double deltaHeight = predictedHoverLocation.getY() - currentLocation.getY();
        Vector chaseVelocityDirection;
        if (chhLength > hoverRadius)
        {
            double chhvTheta = Math.asin(hoverRadius / chhLength);
            double chhvCross = chhNorm.getX() * vhNorm.getZ() - chhNorm.getZ() * vhNorm.getX();
            chaseVelocityDirection = chhNorm.clone().rotateAroundY(chhvTheta * (chhvCross < 0.0 ? 1.0 : -1.0));
        }
        else
        {
            chaseVelocityDirection = vhNorm.clone();
        }
        float chaseSpeed = MathUtils.squareMap(chhLength, hoverRadius, chaseDistance, hoverSpeed, maxSpeed);

        if (deltaHeight > heightTolerance)
        {
            chaseVelocityDirection.add(upDirection);
        }
        else if (deltaHeight < -heightTolerance)
        {
            chaseVelocityDirection.subtract(upDirection);
        }
        chaseVelocityDirection.normalize();
        Vector chaseVelocity = chaseVelocityDirection.clone().multiply(chaseSpeed);

        // CAS
        Vector forwardDirection = _currentVelocity.isZero() ? new Vector(0, 0, 1) : _currentVelocity.clone()
                                                                                                    .normalize();
        Vector rightDirection = forwardDirection.getCrossProduct(upDirection).normalize();
        Vector CASDirection = new Vector(0.0, 0.0, 0.0);
        boolean CASFlag = false;
        for (int radarScanHorizontal = 0; radarScanHorizontal < radarScanStepCountHorizontal; radarScanHorizontal++)
        {
            for (int radarScanVertical = 0; radarScanVertical < radarScanStepCountVertical; radarScanVertical++)
            {
                // calculate radar ray
                double radarAngleVerticalOffset = radarScanStepVertical * radarScanVertical - radarAngleVertical / 2.0;
                double radarAngleHorizontalOffset = radarScanStepHorizontal * radarScanHorizontal - radarAngleHorizontal / 2.0;
                Vector radarRayDirection = forwardDirection.clone()
                                                           .rotateAroundAxis(rightDirection, radarAngleVerticalOffset)
                                                           .rotateAroundY(radarAngleHorizontalOffset);
                // update radar flags
                if (radarScanHorizontal == lastRadarScanStep)
                {
                    double theta = Math.max(Math.abs(radarAngleVerticalOffset), Math.abs(radarAngleHorizontalOffset));
                    Vector radarRay = radarRayDirection.clone()
                                                       .multiply(1.0f + (_currentVelocity.length() * radarRangeFrontMultiplier * Math.pow(Math.max(Math.cos(theta) - 0.5, 0.0), 3)) + (radarRangeSide * Math.sin(theta)));
                    boolean rayResult = (MathUtils.RayTrace(currentLocation, radarRay) != null);
                    radarFlags[radarScanVertical][radarScanHorizontal] = rayResult;
                    if (rayResult && showRadarRayRedParticle)
                    {
                        MathUtils.ForLine(currentLocation, currentLocation.clone()
                                                                          .add(radarRay), 0.2f, (location, progress) -> location.getWorld()
                                                                                                                                .spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(Color.RED, 0.4f)));
                    }
                    else if (showRadarRayGreenParticle)
                    {
                        MathUtils.ForLine(currentLocation, currentLocation.clone()
                                                                          .add(radarRay), 0.2f, (location, progress) -> location.getWorld()
                                                                                                                                .spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(Color.GREEN, 0.4f)));
                    }
                }
                // apply direction
                if (radarFlags[radarScanVertical][radarScanHorizontal])
                {
                    CASDirection.subtract(radarRayDirection);
                    CASFlag = true;
                }
            }
        }
        lastRadarScanStep = (lastRadarScanStep + 1) % radarScanStepCountHorizontal;

        Vector CASVelocity = CASFlag ? CASDirection.clone()
                                                   .normalize()
                                                   .multiply(MathUtils.Lerp(CASSpeedMin, CASSpeedMax, (float) currentVel.length() / maxSpeed)) : null;

        Vector targetVelocity = attackFlag
                ? (CASFlag ? MathUtils.Lerp(attackVelocity, CASVelocity, CASWeightWhenAttacking) : attackVelocity)
                : (CASFlag ? MathUtils.Lerp(chaseVelocity, CASVelocity, CASWeight) : chaseVelocity);

        // calculate resultant force
        Vector nextCameraVelocity = MathUtils.Lerp(_currentVelocity, targetVelocity, 0.05f);
        if (showPropellerParticle)
        {
            Location particleStartLocation = currentLocation.clone().add(_currentVelocity.clone().multiply(-0.15));
            Vector force = _currentVelocity.clone()
                                           .subtract(nextCameraVelocity)
                                           .multiply(2.0)
                                           .add(new Vector(0.0, -1.0, 0.0));
            float forceLevel = MathUtils.Clamp((float) force.length() / (maxSpeed / 10.0f), 0.0f, 1.0f);
            Location particleEndLocation = particleStartLocation.clone().add(force);
            float minParticleSize = MathUtils.Lerp(0.5f, 1.0f, forceLevel);
            float maxParticleSize = MathUtils.Lerp(1.5f, 3.0f, forceLevel);
            MathUtils.ForLine(particleStartLocation, particleEndLocation, 0.2f, (location, progress) -> {
                float particleSize = MathUtils.Lerp(maxParticleSize, minParticleSize, progress);
                float volume = MathUtils.Lerp(1.5f, 2.0f, forceLevel);
                float pitch = MathUtils.Lerp(0.5f, 2.0f, forceLevel);

                currentLocation.getWorld()
                               .spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(Color.WHITE, particleSize));
                if (playPropellerSound)
                {
                    currentLocation.getWorld()
                                   .playSound(currentLocation, Sound.BLOCK_GRASS_STEP, SoundCategory.RECORDS, volume, pitch);
                }
            });
        }

        // apply force and velocity
        _currentVelocity = nextCameraVelocity;
        _location.add(_currentVelocity.clone().multiply(deltaTime));

        // set rotation
        Location lookatLocation = targetLocation.clone().add(predictedOffset.clone().multiply(predLookAtWeight));
        MathUtils.LookAt(_location, lookatLocation);

        // update last focus position for next tick
        lastTargetLocation = targetLocation.clone();

        // particles
        _location.getWorld().spawnParticle(Particle.FIREWORK, _location, 2);
        if(attackFlag)
        {
            MathUtils.ForLine(_location, _targetGHPlayer.getBodyLocation(), 0.2f, (location, progress) -> location.getWorld()
                                                                                                                  .spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(Color.RED, 0.4f)));
        }
        // detonate if reached target or hit ground
        if(_location.distance(_targetGHPlayer.getBodyLocation()) < explodeDistance || !_location.getWorld().getBlockAt(_location).getType().isAir())
        {
            _location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, _location, 1);
            _location.getWorld().playSound(_location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // get all gh players in range and drop score of them
            int totalDroppedScore = 0;
            double eNeg1 = Math.exp(-1.0);
            for(GHPlayer victimGHPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
            {
                double d = victimGHPlayer.getBodyLocation().distance(_location);
                if(d <= explodeRange)
                {
                    double x = Math.clamp(d / explodeRange, 0.0, 1.0);
                    double w = (Math.exp(-Math.pow(x, 2.0)) - eNeg1) / (1.0 - eNeg1);

                    int dropScore = Math.min(victimGHPlayer.getScore(), Math.round(victimGHPlayer.getScore() * explodeDropScore * (float) w));
                    if(dropScore > 0)
                    {
                        victimGHPlayer.addScore(-dropScore);
                        victimGHPlayer.getPlayer().damage(1.0);
                        gameInstance.getEntityManager().addEntity(new ScoreOrb(_location, victimGHPlayer, null, dropScore));
                        LazuliUI.sendMessage(victimGHPlayer.getPlayer(), MessageFactory.getBlewByBombDroneMsg(_sourceGHPlayer, dropScore));
                        LazuliUI.sendMessage(_sourceGHPlayer.getPlayer(), MessageFactory.getDroneBlewMsg(victimGHPlayer, dropScore));
                        totalDroppedScore += dropScore;
                    }
                }
            }
            LazuliUI.broadcast(MessageFactory.getDroneExplodedBroadcastMsg(_sourceGHPlayer, totalDroppedScore));

            _isDestroyed = true;
        }
    }
}