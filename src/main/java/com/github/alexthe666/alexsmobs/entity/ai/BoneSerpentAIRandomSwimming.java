package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class BoneSerpentAIRandomSwimming extends RandomWalkingGoal {
    public BoneSerpentAIRandomSwimming(CreatureEntity creature, double speed, int chance) {
        super(creature, speed, chance, false);
    }

    public boolean shouldExecute() {
        if (this.creature.isBeingRidden() || creature.getAttackTarget() != null) {
            return false;
        } else {
            if (!this.mustUpdate) {
                if (this.creature.getRNG().nextInt(this.executionChance) != 0) {
                    return false;
                }
            }
            Vector3d vector3d = this.getPosition();
            if (vector3d == null) {
                return false;
            } else {
                this.x = vector3d.x;
                this.y = vector3d.y;
                this.z = vector3d.z;
                this.mustUpdate = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vector3d getPosition() {
        if(this.creature.getRNG().nextFloat() < (this.creature.isInLava() ? 0.7F : 0.3F)){
            Vector3d vector3d = findSurfaceTarget(this.creature, 32, 16);
            if(vector3d != null){
                return vector3d;
            }
        }
        Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 32, 16);

        for(int i = 0; vector3d != null && !this.creature.world.getBlockState(new BlockPos(vector3d)).allowsMovement(this.creature.world, new BlockPos(vector3d), PathType.WATER) && i++ < 10; vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7)) {
        }

        return vector3d;
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
        return (this.creature.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.creature.world.getBlockState(blockpos).getMaterial().blocksMovement() || this.creature.world.getFluidState(blockpos).isTagged(FluidTags.LAVA));
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.creature.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.creature.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
    }

    private Vector3d findSurfaceTarget(CreatureEntity creature, int i, int i1) {
        Vector3d creaturePos = creature.getPositionVec();
        BlockPos upPos = creature.getPosition();
        while(creature.world.getFluidState(upPos).isTagged(FluidTags.LAVA) || creature.world.getFluidState(upPos).isTagged(FluidTags.WATER)){
            upPos = upPos.up();
        }
        if(isAirAbove(upPos.down(), 0, 0, 0) && canJumpTo(upPos.down(), 0, 0, 0)){
            return new Vector3d(upPos.getX() + 0.5F, upPos.getY() + 3.5F, upPos.getZ() + 0.5F);
        }
        return null;
    }
}
