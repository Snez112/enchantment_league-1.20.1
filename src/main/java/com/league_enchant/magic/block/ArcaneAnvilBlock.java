package com.league_enchant.magic.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ArcaneAnvilBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.1, 0.0, 0.1, 0.9, 0.8, 0.9);

    public ArcaneAnvilBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(new net.minecraft.screen.SimpleNamedScreenHandlerFactory(
                (syncId, inv, p) -> new com.league_enchant.magic.screen.ArcaneAnvilScreenHandler(syncId, inv),
                Text.literal("Đế Đe Rèn Phép Arcane Anvil")
            ));
        }
        return ActionResult.SUCCESS;
    }
}
