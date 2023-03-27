package net.mehvahdjukaar.sleep_tight.common.blocks;

import net.mehvahdjukaar.sleep_tight.configs.CommonConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface IModBed extends ISleepTightBed {

    Vec3 getSleepingPosition(BlockState state, BlockPos pos);

    default void onLeftBed(BlockState state, BlockPos pos, Player entity) {
    }

    boolean canSetSpawn();

    default Component getSleepingProblemMessage() {
        return Component.empty();
    }

    default InteractionResult canSleepAtTime(Level level) {
        return InteractionResult.PASS;
    }

    static boolean tryExploding(Level level, BlockPos pos) {
        if (!BedBlock.canSetSpawn(level)) {
            if (!level.isClientSide) {
                var c = CommonConfigs.EXPLOSION_BEHAVIOR.get();
                if (c == CommonConfigs.ExplosionBehavior.ALLOWS_SLEEPING) return false;
                level.removeBlock(pos, false);
                float size = c == CommonConfigs.ExplosionBehavior.TINY_EXPLOSION ? 0 : 5.0F;
                level.explode(null, level.damageSources().badRespawnPointExplosion(pos.getCenter()),
                        null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        size, true, Level.ExplosionInteraction.BLOCK);
            }
            return true;
        }
        return false;
    }
}
