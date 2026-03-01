package com.whatever.infiniterailway;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RailwayFeature extends Feature<NoneFeatureConfiguration> {
	public RailwayFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
		WorldGenLevel level = ctx.level();
		var random = ctx.random();
		BlockPos origin = ctx.origin(); // this is chunk-based for placed features

		// Pick a start position inside this chunk (example)
		int x = origin.getX() + random.nextInt(16);
		int z = origin.getZ() + random.nextInt(16);

		// Decide Y: often use heightmap so you sit on the surface
		int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, x, z);

		// Run your algorithm; here's a trivial straight line example:
		int length = 24;
		int dx = random.nextBoolean() ? 1 : 0;
		int dz = dx == 1 ? 0 : 1;

		boolean placedAny = false;

		for (int i = 0; i < length; i++) {
			int px = x + i * dx;
			int pz = z + i * dz;
			int py = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG,
					px, pz);

			BlockPos railPos = new BlockPos(px, py, pz);

			// Basic “ensure ground” (ballast) then rail
			BlockPos below = railPos.below();
			if (level.isEmptyBlock(below))
				continue; // skip floating

			level.setBlock(below, Blocks.GRAVEL.defaultBlockState(), 2);
			level.setBlock(railPos, Blocks.RAIL.defaultBlockState(), 2);
			placedAny = true;
		}

		return placedAny;
	}
}
