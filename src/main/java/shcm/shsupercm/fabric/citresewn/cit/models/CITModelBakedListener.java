package shcm.shsupercm.fabric.citresewn.cit.models;

import net.minecraft.client.render.model.BakedModel;

@FunctionalInterface
public interface CITModelBakedListener {
    void citModelBaked(CITModel citModel, BakedModel bakedModel);
}
