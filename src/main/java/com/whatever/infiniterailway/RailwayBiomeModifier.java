package com.whatever.infiniterailway;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record RailwayBiomeModifier(
		HolderSet<Biome> biomes,
		HolderSet<PlacedFeature> features,
		GenerationStep.Decoration step) implements BiomeModifier {

	public static final MapCodec<RailwayBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(
					Biome.LIST_CODEC.fieldOf("biomes").forGetter(RailwayBiomeModifier::biomes),
					PlacedFeature.LIST_CODEC.fieldOf("features")
							.forGetter(RailwayBiomeModifier::features),
					StringRepresentable.fromEnum(GenerationStep.Decoration::values)
							.fieldOf("step")
							.forGetter(RailwayBiomeModifier::step))
			.apply(instance, RailwayBiomeModifier::new));

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
		if (phase != Phase.ADD)
			return;
		if (!biomes.contains(biome))
			return;

		var gen = builder.getGenerationSettings();
		for (Holder<PlacedFeature> feature : features) {
			gen.addFeature(step, feature);
		}
	}

	@Override
	public MapCodec<? extends BiomeModifier> codec() {
		return CODEC;
	}
}
