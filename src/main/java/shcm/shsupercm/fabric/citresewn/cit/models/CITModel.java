package shcm.shsupercm.fabric.citresewn.cit.models;

import com.google.common.collect.Sets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.cit.CITType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

public final class CITModel implements CITModelBakedListener {
    public static final String ABSOLUTE_PATH_NAMESPACE = "citresewn";

    public final Identifier propertiesIdentifier, modelLoaderIdentifier;
    private JsonUnbakedModel mainModel = null;

    private CITModelBakedListener bakedListener = null;

    public CITModel(Identifier propertiesIdentifier) {
        this.propertiesIdentifier = propertiesIdentifier;
        this.modelLoaderIdentifier = new Identifier(ABSOLUTE_PATH_NAMESPACE, propertiesIdentifier.getNamespace() + "/" + propertiesIdentifier.getPath());
    }

    public JsonUnbakedModel mainModel() {
        if (this.mainModel == null)
            throw new NullPointerException("No main model assigned");
        return this.mainModel;
    }

    public CITModel mainModel(JsonUnbakedModel unbakedModel) throws ModelReadException {
        if (this.mainModel != null)
            throw new ModelReadException(new IllegalStateException("Already set main model"));

        this.mainModel = unbakedModel;
        this.mainModel.id = modelLoaderIdentifier.toString();
        ((ModelWithCITModel) this.mainModel).citresewn$setCITModel(this);
        return this;
    }

    public CITModel mainModel(Reader json) throws ModelReadException {
        try {
            return mainModel(JsonUnbakedModel.deserialize(json));
        } catch (Exception e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel mainModel(Identifier resourcePath) throws ModelReadException {
        try {
            BufferedReader reader = MinecraftClient.getInstance().getResourceManager().getResource(resourcePath).orElseThrow().getReader();
            return mainModel(reader);
        } catch (IOException | NoSuchElementException e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel mainModel(Item baseModel) throws ModelReadException {
        Identifier itemIdentifier = Registries.ITEM.getId(baseModel);
        if (Registries.ITEM.getDefaultId().equals(itemIdentifier) && baseModel != Items.AIR)
            throw new ModelReadException(new NullPointerException("Item not in registry"));

        try {
            return mainModel(CITResewn.INSTANCE.activeModelLoader.loadModelFromJson(new Identifier(itemIdentifier.getNamespace(), "item/" + itemIdentifier.getPath())));
        } catch (IOException e) {
            throw new ModelReadException(e);
        }
    }

    public CITModel resolveParentTree() throws ModelReadException {
        LinkedHashSet<JsonUnbakedModel> set = Sets.newLinkedHashSet();
        JsonUnbakedModel jsonUnbakedModel = mainModel();
        while (jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null) {
            set.add(jsonUnbakedModel);
            JsonUnbakedModel parentUnbakedModel;
            if (jsonUnbakedModel.parentId.getPath().equals("builtin/generated"))
                parentUnbakedModel = ModelLoader.GENERATION_MARKER;
            else {
                Identifier resolvedParentIdentifier = CITType.resolveAsset(this.propertiesIdentifier, jsonUnbakedModel.parentId.toString(), "models", ".json", MinecraftClient.getInstance().getResourceManager());
                parentUnbakedModel = new CITModel(ModelLoader.MISSING_ID).mainModel(resolvedParentIdentifier).mainModel;
                if (parentUnbakedModel == null) {
                    throw new ModelReadException(new NullPointerException("Parent '" + jsonUnbakedModel.parentId.toString() + "' not found"));
                }
                if (set.contains(parentUnbakedModel)) {
                    throw new ModelReadException(new IllegalStateException("Found parent loop '" + jsonUnbakedModel.parentId.toString() + "'"));
                }
            }
            jsonUnbakedModel.parent = parentUnbakedModel;
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        return this;
    }

    public CITModel resolveOverrides() {
        for (ModelOverride override : mainModel().getOverrides()) {
            Identifier resolvedOverrideIdentifier = CITType.resolveAsset(this.propertiesIdentifier, override.getModelId().toString(), "models", ".json", MinecraftClient.getInstance().getResourceManager());
            for (ModelOverride.Condition condition : override.conditions) {
                //stub todo
            }
        }
        mainModel().getOverrides().clear();
        return this;
    }

    public CITModel bake(CITModelBakedListener bakedListener) {
        if (mainModel == null)
            throw new IllegalStateException("No main model assigned");

        this.bakedListener = bakedListener;

        CITResewn.INSTANCE.activeModelLoader.unbakedModels.put(modelLoaderIdentifier, mainModel);
        CITResewn.INSTANCE.activeModelLoader.modelsToBake.put(modelLoaderIdentifier, mainModel);
        this.mainModel = null;
        return this;
    }

    @Override
    public void citModelBaked(CITModel citModel, BakedModel bakedModel) {
        bakedListener.citModelBaked(citModel, bakedModel);
    }
}
