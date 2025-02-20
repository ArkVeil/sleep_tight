package net.mehvahdjukaar.sleep_tight.integration.forge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.building.block.WoodPostBlock;

public class QuarkCompatImpl {
    public static boolean isVerticalPost(BlockState facingState) {
        return facingState.getBlock() instanceof WoodPostBlock && facingState.getValue(WoodPostBlock.AXIS) == Direction.Axis.Y;
    }
}
